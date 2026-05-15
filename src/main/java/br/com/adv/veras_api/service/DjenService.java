package br.com.adv.veras_api.service;

import br.com.adv.veras_api.client.ComunicaPjeClient;
import br.com.adv.veras_api.client.DjenDownloadClient;
import br.com.adv.veras_api.dto.AdvogadoDjenResponse;
import br.com.adv.veras_api.dto.CadernoDjenResponse;
import br.com.adv.veras_api.dto.ComunicacaoDjenResponse;
import br.com.adv.veras_api.dto.DestinatarioAdvogadoDjenResponse;
import br.com.adv.veras_api.entity.Intimacao;
import br.com.adv.veras_api.repository.IntimacaoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class DjenService {

    private final ObjectMapper objectMapper;
    private final IntimacaoRepository intimacaoRepository;
    private final ComunicaPjeClient comunicaPjeClient;
    private final DjenDownloadClient djenDownloadClient;

    public void importarIntimacoesPorOab(
            String siglaTribunal,
            LocalDate data,
            String meio,
            String numeroOab,
            String ufOab
    ) {
        try {
            CadernoDjenResponse caderno = comunicaPjeClient.buscarCaderno(
                    siglaTribunal,
                    data.toString(),
                    meio
            );

            if (caderno == null || caderno.getUrl() == null) {
                throw new RuntimeException("Caderno não encontrado para a data informada.");
            }

            byte[] zipBytes = baixarZip(caderno.getUrl());

            if (zipBytes.length == 0) {
                throw new RuntimeException("Erro ao baixar ZIP do DJEN.");
            }

            processarZip(zipBytes, numeroOab, ufOab);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar intimações do DJEN: " + e.getMessage(), e);
        }
    }

    private byte[] baixarZip(String urlZip) throws IOException {
        try (Response response = djenDownloadClient.baixarZip(URI.create(urlZip))) {

            if (response.status() < 200 || response.status() >= 300) {
                throw new RuntimeException("Erro ao baixar ZIP. Status: " + response.status());
            }

            if (response.body() == null) {
                throw new RuntimeException("ZIP veio vazio.");
            }

            return response.body().asInputStream().readAllBytes();
        }
    }

    private void processarZip(
            byte[] zipBytes,
            String numeroOab,
            String ufOab
    ) throws IOException {

        int totalArquivosJson = 0;
        int totalComunicacoes = 0;
        int totalComAdvogados = 0;
        int totalEncontrados = 0;

        System.out.println("ZIP TAMANHO: " + zipBytes.length);
        System.out.println("BUSCANDO OAB: " + numeroOab + " / UF: " + ufOab);

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {

                if (entry.isDirectory() || !entry.getName().endsWith(".json")) {
                    continue;
                }

                totalArquivosJson++;

                byte[] jsonBytes = zis.readAllBytes();

                JsonNode root = objectMapper.readTree(jsonBytes);
                JsonNode items = root.get("items");

                if (items == null || !items.isArray()) {
                    System.out.println("Arquivo sem items: " + entry.getName());
                    continue;
                }

                System.out.println("ARQUIVO NO ZIP: " + entry.getName());
                System.out.println("QTD COMUNICAÇÕES NO ARQUIVO: " + items.size());

                for (JsonNode item : items) {
                    ComunicacaoDjenResponse comunicacao =
                            objectMapper.treeToValue(item, ComunicacaoDjenResponse.class);

                    totalComunicacoes++;

                    if (comunicacao.getDestinatarioadvogados() != null) {
                        for (DestinatarioAdvogadoDjenResponse dest : comunicacao.getDestinatarioadvogados()) {
                            if (dest.getAdvogado() != null) {
                                totalComAdvogados++;

                                System.out.println("ADVOGADO: "
                                        + dest.getAdvogado().getNome()
                                        + " | OAB: " + dest.getAdvogado().getNumeroOab()
                                        + " | UF: " + dest.getAdvogado().getUfOab());
                            }
                        }
                    }

                    if (pertenceAoAdvogado(comunicacao, numeroOab, ufOab)) {
                        totalEncontrados++;
                        System.out.println(">>> ENCONTRADO! Salvando comunicação " + comunicacao.getId());
                        salvarIntimacao(comunicacao, numeroOab, ufOab);
                    }
                }
            }
        }

        System.out.println("TOTAL ARQUIVOS JSON: " + totalArquivosJson);
        System.out.println("TOTAL COMUNICAÇÕES: " + totalComunicacoes);
        System.out.println("TOTAL COM ADVOGADOS: " + totalComAdvogados);
        System.out.println("TOTAL ENCONTRADOS: " + totalEncontrados);
    }

    private boolean pertenceAoAdvogado(
            ComunicacaoDjenResponse comunicacao,
            String numeroOab,
            String ufOab
    ) {
        if (comunicacao.getDestinatarioadvogados() == null) {
            return false;
        }

        return comunicacao.getDestinatarioadvogados()
                .stream()
                .anyMatch(destinatarioAdvogado -> {
                    AdvogadoDjenResponse advogado = destinatarioAdvogado.getAdvogado();

                    return advogado != null
                            && normalizar(advogado.getNumeroOab()).equals(normalizar(numeroOab))
                            && normalizar(advogado.getUfOab()).equals(normalizar(ufOab));
                });
    }

    private void salvarIntimacao(
            ComunicacaoDjenResponse comunicacao,
            String numeroOab,
            String ufOab
    ) {
        boolean jaExiste = intimacaoRepository.existsByComunicacaoDjenId(comunicacao.getId());

        if (jaExiste) {
            return;
        }

        AdvogadoDjenResponse advogadoEncontrado = buscarAdvogadoNaComunicacao(
                comunicacao,
                numeroOab,
                ufOab
        );

        Intimacao intimacao = new Intimacao();

        intimacao.setComunicacaoDjenId(comunicacao.getId());
        intimacao.setSiglaTribunal(comunicacao.getSiglaTribunal());
        intimacao.setTipoComunicacao(comunicacao.getTipoComunicacao());
        intimacao.setNumeroProcesso(comunicacao.getNumeroProcesso());
        intimacao.setNumeroProcessoComMascara(comunicacao.getNumeroprocessocommascara());
        intimacao.setNomeOrgao(comunicacao.getNomeOrgao());
        intimacao.setTipoDocumento(comunicacao.getTipoDocumento());
        intimacao.setNomeClasse(comunicacao.getNomeClasse());
        intimacao.setDataDisponibilizacao(comunicacao.getDataDisponibilizacao());
        intimacao.setTexto(comunicacao.getTexto());
        intimacao.setLink(comunicacao.getLink());
        intimacao.setHash(comunicacao.getHash());

        if (advogadoEncontrado != null) {
            intimacao.setNomeAdvogado(advogadoEncontrado.getNome());
            intimacao.setNumeroOab(advogadoEncontrado.getNumeroOab());
            intimacao.setUfOab(advogadoEncontrado.getUfOab());
        }

        intimacaoRepository.save(intimacao);
    }

    private AdvogadoDjenResponse buscarAdvogadoNaComunicacao(
            ComunicacaoDjenResponse comunicacao,
            String numeroOab,
            String ufOab
    ) {
        if (comunicacao.getDestinatarioadvogados() == null) {
            return null;
        }

        return comunicacao.getDestinatarioadvogados()
                .stream()
                .map(DestinatarioAdvogadoDjenResponse::getAdvogado)
                .filter(advogado -> advogado != null
                        && normalizar(advogado.getNumeroOab()).equals(normalizar(numeroOab))
                        && normalizar(advogado.getUfOab()).equals(normalizar(ufOab)))
                .findFirst()
                .orElse(null);
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.trim().replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }
}
