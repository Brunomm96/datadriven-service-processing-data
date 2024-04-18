package br.com.datawake.datadrivenserviceprocessingdata;

import br.com.datawake.datadrivenserviceprocessingdata.infrastructure.repository.CustomJpaRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl.class)
public class DatadrivenServiceProcessingDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatadrivenServiceProcessingDataApplication.class, args);
	}

	@Bean
	public WebClient webClientTelegram(WebClient.Builder builder) {
		return builder
				.baseUrl("https://api.telegram.org")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

}
