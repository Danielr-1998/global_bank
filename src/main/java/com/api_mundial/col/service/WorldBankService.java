package com.api_mundial.col.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WorldBankService {

    private final WebClient webClient;

    public WorldBankService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://restcountries.com/v3.1").build();
    }


    public Mono<String> obtenerDatosPais(String pais) {
        String url = "/name/" + pais;
        return this.webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class);
    }


}
