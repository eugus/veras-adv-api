package br.com.adv.veras_api.client;


import br.com.adv.veras_api.dto.CadernoDjenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "comunicaPjeClient",
        url = "https://comunicaapi.pje.jus.br"
)
public interface ComunicaPjeClient {

    @GetMapping("/api/v1/caderno/{tribunal}/{data}/{meio}")
    CadernoDjenResponse buscarCaderno(
            @PathVariable String tribunal,
            @PathVariable String data,
            @PathVariable String meio
    );
}
