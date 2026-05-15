package br.com.adv.veras_api.service;


import br.com.adv.veras_api.dto.AnaliseIntimacaoResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiIntimacaoService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public GeminiIntimacaoService(
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model,
            ObjectMapper objectMapper
    ) {
        this.model = model;
        this.objectMapper = objectMapper;

        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .defaultHeader("x-goog-api-key", apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private String sanitize(String texto) {
        return texto
                .replace("\\", "")
                .replace("\"", "'")
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();
    }

    public AnaliseIntimacaoResponse analisar(String texto) {

        String textoLimpo = sanitize(texto);
        String prompt = """
        Analise a intimação jurídica abaixo e retorne APENAS JSON válido.

        Formato obrigatório:
        {
          "resumo": "",
          "explicacaoSimples": "",
          "tipoEvento": "AUDIENCIA | PRAZO | SENTENCA | DESPACHO | OUTRO",
          "prazoDiasUteis": null,
          "acaoRecomendada": "",
          "urgencia": "BAIXA | MEDIA | ALTA",
          "observacao": "Análise sugerida por IA. Validar com advogado."
        }

        Regras:
        - Não use markdown.
        - Não use ```json.
        - Não invente prazo.
        - Se o prazo não estiver claro, use null.

        Texto:
        %s
        """.formatted(textoLimpo);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "maxOutputTokens", 3000,
                        "responseMimeType", "application/json"
                )
        );

        String response = restClient.post()
                .uri("/models/" + model + ":generateContent")
                .body(body)
                .retrieve()
                .body(String.class);

        try {
            JsonNode root = objectMapper.readTree(response);

            String content = root
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            return objectMapper.readValue(content, AnaliseIntimacaoResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao interpretar resposta do Gemini", e);
        }


    }
}
