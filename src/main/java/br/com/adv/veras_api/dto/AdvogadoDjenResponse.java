package br.com.adv.veras_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdvogadoDjenResponse {

    private Long id;

    private String nome;

    @JsonProperty("numero_oab")
    private String numeroOab;

    @JsonProperty("uf_oab")
    private String ufOab;
}
