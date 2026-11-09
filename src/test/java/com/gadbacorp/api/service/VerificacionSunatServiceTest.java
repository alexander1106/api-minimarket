package com.gadbacorp.api.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gadbacorp.api.service.jpa.administrable.VerificacionSunatService;
import com.gadbacorp.api.service.jpa.compras.ApiService;

import reactor.core.publisher.Mono;

class VerificacionSunatServiceTest {

    private final ApiService api = mock(ApiService.class);
    private final VerificacionSunatService service = new VerificacionSunatService(api, new ObjectMapper(), "token");

    @Test
    void confirmaCuandoLaRazonSocialCoincideIgnorandoTildesYSignos() {
        when(api.consumirApi("20609182341")).thenReturn(Mono.just("{\"razonSocial\":\"24 HORAS MARKET CHICLAYO S.A.C.\"}"));
        assertTrue(service.verificar("20609182341", "24 Horas Market Chiclayo SAC"));
    }

    @Test
    void noConfirmaSiLaRazonSocialEsOtra() {
        when(api.consumirApi("20609182341")).thenReturn(Mono.just("{\"razonSocial\":\"OTRA EMPRESA SAC\"}"));
        assertFalse(service.verificar("20609182341", "24 Horas Market Chiclayo"));
    }

    @Test
    void noConfirmaSiSunatNoDevuelveDatos() {
        when(api.consumirApi("20000000001")).thenReturn(Mono.just("{}"));
        assertFalse(service.verificar("20000000001", "GADBACORP"));
        when(api.consumirApi("20000000002")).thenReturn(Mono.empty());
        assertFalse(service.verificar("20000000002", "GADBACORP"));
    }

    @Test
    void rechazaUnRucMalFormado() {
        assertThrows(IllegalArgumentException.class, () -> service.verificar("123", "X"));
        assertThrows(IllegalArgumentException.class, () -> service.verificar(null, "X"));
    }

    @Test
    void siSunatFallaSeInformaComoErrorDeLaConsulta() {
        when(api.consumirApi("20609182341")).thenReturn(Mono.error(new RuntimeException("503")));
        assertThrows(IllegalStateException.class, () -> service.verificar("20609182341", "X"));
    }

    @Test
    void sinTokenNoEstaConfigurado() {
        assertFalse(new VerificacionSunatService(api, new ObjectMapper(), "").estaConfigurado());
        assertTrue(service.estaConfigurado());
    }
}
