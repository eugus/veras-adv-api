package br.com.adv.veras_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DestinatarioDjenResponse {

    private String nome;
    private String polo;

    @JsonProperty("comunicacao_id")
    private Long comunicacaoId;
}
