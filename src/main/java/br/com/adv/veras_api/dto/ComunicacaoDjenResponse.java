package br.com.adv.veras_api.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComunicacaoDjenResponse {

    private Long id;

    @JsonProperty("data_disponibilizacao")
    private LocalDate dataDisponibilizacao;

    private String siglaTribunal;
    private String tipoComunicacao;
    private String nomeOrgao;
    private String texto;

    @JsonProperty("numero_processo")
    private String numeroProcesso;

    private String meio;
    private String link;
    private String tipoDocumento;
    private String nomeClasse;
    private String numeroComunicacao;
    private String numeroprocessocommascara;
    private Boolean ativo;
    private String hash;
    private String status;

    private List<DestinatarioDjenResponse> destinatarios = new ArrayList<>();

    @JsonProperty("destinatarioadvogados")
    private List<DestinatarioAdvogadoDjenResponse> destinatarioadvogados = new ArrayList<>();
}
