package br.com.adv.veras_api.dto;

import jakarta.validation.constraints.NotBlank;

public record AnaliseIntimacaoRequest(@NotBlank
                                      String texto) {
}
