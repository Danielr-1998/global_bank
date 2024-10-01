package com.api_mundial.col.controller;

import com.api_mundial.col.service.CountryComparisonService;
import com.api_mundial.col.service.WorldBankService;
import com.api_mundial.col.service.CountryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WorldBankController {

    private final WorldBankService worldBankService;
    private final CountryComparisonService countryComparisonService;
    private final CountryService countryService;

    @Autowired
    public WorldBankController(WorldBankService worldBankService, CountryComparisonService countryComparisonService,CountryService countryService) {
        this.worldBankService = worldBankService;
        this.countryComparisonService = countryComparisonService;
        this.countryService=countryService;
    }

    // Ruta para consultar cualquier país basado en el nombre
    @GetMapping("/pais/{nombre}")
    public Mono<String> obtenerDatosPais(@PathVariable String nombre) {
        return worldBankService.obtenerDatosPais(nombre);
    }

    // Comparar países y devolver la comparación en formato texto
    @GetMapping("/comparar-paises/{pais1}/{pais2}")
    public Mono<String> compararPaises(@PathVariable String pais1, @PathVariable String pais2) {
        Mono<String> pais1Data = obtenerDatosPais(pais1);
        Mono<String> pais2Data = obtenerDatosPais(pais2);

        // Combinar los datos de ambos países
        return Mono.zip(pais1Data, pais2Data)
                .map(tuple -> "Datos del país 1: " + tuple.getT1() + "\n\nDatos del país 2: " + tuple.getT2());
    }

    // Generar PDF comparativo entre dos países
    @GetMapping("/comparar-paises/pdf/{pais1}/{pais2}")
    public Mono<ResponseEntity<byte[]>> generarPdfComparacion(@PathVariable String pais1, @PathVariable String pais2) {
        // Obtener los datos de ambos países
        Mono<String> pais1Data = obtenerDatosPais(pais1);
        Mono<String> pais2Data = obtenerDatosPais(pais2);

        // Combinar ambos Monos para generar el PDF cuando ambos estén disponibles
        return Mono.zip(pais1Data, pais2Data)
                .flatMap(tuple -> {
                    byte[] pdfBytes = countryComparisonService.generarPdfComparacion(pais1, pais2, tuple.getT1(), tuple.getT2());
                    return Mono.just(
                            ResponseEntity.ok()
                                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comparacion_" + pais1 + "_vs_" + pais2 + ".pdf")
                                    .contentType(MediaType.APPLICATION_PDF)
                                    .body(pdfBytes)
                    );
                });
    }

    @GetMapping("/prediccion-pib/{pais}")
    public Mono<String> predecirPib(@PathVariable String pais) {
        // Suposición: El código del país es el código ISO de dos letras (ej: "CO" para Colombia)
        return countryService.obtenerDatosPais(pais)
                .flatMap(paisData -> {
                    // Simulación de predicción del PIB
                    double pibActual = (double) paisData.get("gdp"); // Obtener el PIB actual
                    double crecimientoEsperado = 0.03;  // Crecimiento anual esperado del 3%
                    int años = 5;
                    double pibFuturo = pibActual * Math.pow((1 + crecimientoEsperado), años); // Fórmula de crecimiento compuesto
                    return Mono.just("El PIB estimado de " + pais + " en " + años + " años es: " + pibFuturo);
                });
    }

    @GetMapping("/inflacion/{pais}/{anio}")
    public Mono<String> obtenerInflacion(@PathVariable String pais, @PathVariable int anio) {
        return countryService.obtenerInflacion(pais, anio);
    }
}