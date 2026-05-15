package br.com.adv.veras_api.client;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;

@FeignClient(
        name = "djenDownloadClient",
        url = "https://djen-prd.prd.s3.cnj.jus.br"
)
public interface DjenDownloadClient {
    @GetMapping
    Response baixarZip(URI uri);
}
