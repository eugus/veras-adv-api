package br.com.adv.veras_api.controller;

import br.com.adv.veras_api.service.DjenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/djen")
@RequiredArgsConstructor
public class DjenController {

    private final DjenService djenService;

    @GetMapping("/importar")
    public String importar(
            @RequestParam String tribunal,
            @RequestParam String data,
            @RequestParam String meio,
            @RequestParam String oab,
            @RequestParam String uf
    ) {

        djenService.importarIntimacoesPorOab(
                tribunal,
                LocalDate.parse(data),
                meio,
                oab,
                uf
        );

        return "Importação finalizada!";
    }
}
