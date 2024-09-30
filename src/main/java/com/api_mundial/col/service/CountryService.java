package com.api_mundial.col.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class CountryService {

    private final WebClient webClient;
     

    public CountryService() {
        this.webClient = WebClient.create("https://api.worldbank.org/v2");
    }

    // Obtener datos generales del país, incluido el PIB
    public Mono<Map<String, Object>> obtenerDatosPais(String paisCode) {
        // Aquí puedes obtener los datos del país desde una API, como la API del Banco Mundial
        return webClient.get()
                .uri("/country/" + paisCode + "/indicator/NY.GDP.MKTP.CD?format=json")
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parsearDatosPais); // Parsear el JSON a un Map
    }

    private Map<String, Object> parsearDatosPais(String jsonData) {
        // Aquí puedes usar una librería como Jackson para convertir el JSON en un Map
        // Simulamos los datos para el PIB
        return Map.of(
                "gdp", 3000000000000.0 // PIB actual en dólares
        );
    }
}
