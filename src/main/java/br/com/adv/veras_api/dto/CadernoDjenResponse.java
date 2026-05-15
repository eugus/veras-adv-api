package br.com.adv.veras_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CadernoDjenResponse {

    private String tribunal;

    @JsonProperty("sigla_tribunal")
    private String siglaTribunal;

    private String meio;

    private String status;

    private Integer versao;

    private LocalDate data;

    @JsonProperty("total_comunicacoes")
    private Integer totalComunicacoes;

    @JsonProperty("numero_paginas")
    private Integer numeroPaginas;

    @JsonProperty("tamanho_bytes")
    private Long tamanhoBytes;

    private String hash;

    private String url;
}
