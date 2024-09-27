package com.api_mundial.col.controller;

import com.api_mundial.col.service.CountryComparisonService;
import com.api_mundial.col.service.WorldBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

@RestController
public class WorldBankController {

    private final WorldBankService worldBankService;
    private final CountryComparisonService countryComparisonService;

    @Autowired
    public WorldBankController(WorldBankService worldBankService, CountryComparisonService countryComparisonService) {
        this.worldBankService = worldBankService;
        this.countryComparisonService = countryComparisonService;
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
}
