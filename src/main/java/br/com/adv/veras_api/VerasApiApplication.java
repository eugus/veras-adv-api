package br.com.adv.veras_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class VerasApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VerasApiApplication.class, args);
	}

}
