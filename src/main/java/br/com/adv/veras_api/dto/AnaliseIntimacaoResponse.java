package br.com.adv.veras_api.dto;

public record AnaliseIntimacaoResponse(String resumo,
                                       String explicacaoSimples,
                                       String tipoEvento,
                                       Integer prazoDiasUteis,
                                       String acaoRecomendada,
                                       String urgencia,
                                       String observacao) {
}
