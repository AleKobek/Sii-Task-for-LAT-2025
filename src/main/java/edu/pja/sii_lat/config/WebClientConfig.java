package edu.pja.sii_lat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        String apiKey = "590f0620afc981a78315873c";
        String url = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/";
        return WebClient.builder()
                .baseUrl(url)
                .build();
    }
}
