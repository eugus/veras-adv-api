package br.com.adv.veras_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DestinatarioAdvogadoDjenResponse {

    private Long id;

    @JsonProperty("comunicacao_id")
    private Long comunicacaoId;

    @JsonProperty("advogado_id")
    private Long advogadoId;

    private AdvogadoDjenResponse advogado;
}
