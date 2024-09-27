package com.api_mundial.col.service;

import com.fasterxml.jackson.databind.JsonNode;
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
    // Nuevo método para calcular el PIB per cápita de un país
    public Mono<String> obtenerPibPerCapita(String pais) {
        String url = "/name/" + pais;
        return this.webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> {
                    // Extraer la población y el PIB del país
                    double pib = jsonNode.get(0).get("gdp").asDouble();
                    int poblacion = jsonNode.get(0).get("population").asInt();
                    double pibPerCapita = pib / poblacion;
                    return "El PIB per cápita de " + pais + " es: " + pibPerCapita;
                });
    }

    // Nuevo método para calcular la densidad poblacional de un país
    public Mono<String> obtenerDensidadPoblacional(String pais) {
        String url = "/name/" + pais;
        return this.webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> {
                    // Extraer el área y la población del país
                    double area = jsonNode.get(0).get("area").asDouble();
                    int poblacion = jsonNode.get(0).get("population").asInt();
                    double densidadPoblacional = poblacion / area;
                    return "La densidad poblacional de " + pais + " es: " + densidadPoblacional + " habitantes por km²";
                });
    }
}
