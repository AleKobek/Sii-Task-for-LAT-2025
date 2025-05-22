package edu.pja.sii_lat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        // enter Your key here
        String apiKey = "YOUR_KEY";
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/";
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }
}
