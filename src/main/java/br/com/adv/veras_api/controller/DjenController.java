package br.com.adv.veras_api.controller;

import br.com.adv.veras_api.service.DjenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/djen")
@RequiredArgsConstructor
public class DjenController {

    private final DjenService djenService;

    @GetMapping("/importar")
    public Map<String, Object> importar(
            @RequestParam List<String> tribunal,
            @RequestParam String dataInicial,
            @RequestParam String dataFinal,
            @RequestParam String meio,
            @RequestParam String oab,
            @RequestParam String uf
    ) {

        return djenService.importarMultiplosTribunais(
                tribunal,
                LocalDate.parse(dataInicial),
                LocalDate.parse(dataFinal),
                meio,
                oab,
                uf
        );
    }
}