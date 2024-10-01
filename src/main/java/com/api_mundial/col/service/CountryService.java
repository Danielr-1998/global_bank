package com.api_mundial.col.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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


    public Mono<String> obtenerInflacion(String pais, int anio) {
        String indicatorCode = "FP.CPI.TOTL.ZG";  // Código de inflación (CPI)

        return webClient.get()
                .uri("/country/{pais}/indicator/{indicador}?date={anio}&format=json", pais, indicatorCode, anio)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    // Parsear la respuesta JSON para obtener la inflación
                    double inflacion = parsearInflacion(response);
                    return Mono.just("La tasa de inflación de " + pais + " en " + anio + " fue de: " + inflacion + "%");
                });
    }

    // Método para parsear la inflación desde la respuesta JSON
    private double parsearInflacion(String jsonResponse) {
        JSONArray jsonArray = new JSONArray(jsonResponse);
        if (jsonArray.length() > 1) {
            JSONObject datos = jsonArray.getJSONObject(1);  // Obtener el segundo objeto que contiene los datos
            if (!datos.isNull("value")) {
                return datos.getDouble("value");  // Obtener el valor de la inflación
            }
        }
        return 0.0;  // Si no se encuentra el dato
    }
    private Map<String, Object> parsearDatosPais(String jsonData) {
        // Aquí puedes usar una librería como Jackson para convertir el JSON en un Map
        // Simulamos los datos para el PIB
        return Map.of(
                "gdp", 3000000000000.0 // PIB actual en dólares
        );
    }
}
