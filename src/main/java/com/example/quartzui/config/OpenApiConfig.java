package com.example.quartzui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
	
	@Bean
	public OpenAPI openAPI() {
		Info info = new Info()
				.title("Quartz Manager API")
				.version("1.0.0")
				.description("Quartz Scheduler 관리를 위한 REST API")
				.contact(new Contact()
						.name("Quartz Manager")
						.url("http://localhost:8080"));
		
		Server localServer = new Server()
				.url("http://localhost:8080")
				.description("로컬 서버");
		
		return new OpenAPI()
				.info(info)
				.addServersItem(localServer);
	}
}
