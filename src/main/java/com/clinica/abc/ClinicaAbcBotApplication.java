package com.clinica.abc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(exclude = { CacheAutoConfiguration.class})
public class ClinicaAbcBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicaAbcBotApplication.class, args);
	}
}
