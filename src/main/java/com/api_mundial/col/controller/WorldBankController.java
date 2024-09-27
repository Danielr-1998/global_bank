package com.api_mundial.col.controller;

import com.api_mundial.col.service.WorldBankService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import reactor.core.publisher.Mono;

@RestController
public class WorldBankController {

    private final WorldBankService worldBankService;

    public WorldBankController(WorldBankService worldBankService) {
        this.worldBankService = worldBankService;
    }

    // Ruta para consultar cualquier país basado en el nombre
    @GetMapping("/pais/{nombre}")
    public Mono<String> obtenerDatosPais(@PathVariable String nombre) {
        return worldBankService.obtenerDatosPais(nombre);
    }

    @GetMapping("/pib-per-capita/{nombre}")
    public Mono<String> obtenerPibPerCapita(@PathVariable String nombre) {
        return worldBankService.obtenerPibPerCapita(nombre);
    }

    @GetMapping("/densidad-poblacional/{nombre}")
    public Mono<String> obtenerDensidadPoblacional(@PathVariable String nombre) {
        return worldBankService.obtenerDensidadPoblacional(nombre);
    }

}
