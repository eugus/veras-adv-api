package br.com.adv.veras_api.controller;


import br.com.adv.veras_api.dto.AnaliseIntimacaoRequest;
import br.com.adv.veras_api.dto.AnaliseIntimacaoResponse;
import br.com.adv.veras_api.service.GeminiIntimacaoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/intimacoes")
public class IntimacaoController {

    private final GeminiIntimacaoService geminiIntimacaoService;

    public IntimacaoController(GeminiIntimacaoService geminiIntimacaoService) {
        this.geminiIntimacaoService = geminiIntimacaoService;
    }

    @PostMapping("/analisar")
    public AnaliseIntimacaoResponse analisar(@RequestBody @Valid AnaliseIntimacaoRequest request) {
        return geminiIntimacaoService.analisar(request.texto());
    }
}
