package com.gadbacorp.api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ProduccionGuardTest {

    private static final String JWT = "un-secreto-propio-de-mas-de-treinta-y-dos-caracteres";
    private static final String CLAVE_AES = "q83QuPdsb0uEH9qGUm2SXZLsT7yTcfhqb9bmuSjZ9Wo=";

    @Test
    void losValoresDeDesarrolloImpidenArrancarEnProduccion() {
        ProduccionGuard guard = new ProduccionGuard(ProduccionGuard.JWT_DESARROLLO, "", true, "1234", "1234", "*");
        assertEquals(4, guard.problemas().size());
        IllegalStateException e = assertThrows(IllegalStateException.class, guard::validar);
        assertTrue(e.getMessage().contains("JWT_SECRET"));
        assertTrue(e.getMessage().contains("APP_CRYPTO_KEY"));
        assertTrue(e.getMessage().contains("CORS_ALLOWED_ORIGINS"));
    }

    @Test
    void unaConfiguracionPropiaArranca() {
        ProduccionGuard guard = new ProduccionGuard(JWT, CLAVE_AES, true, "Claveadmin#2026", "Claveempresa#2026",
                "https://minimarket.netlify.app");
        assertDoesNotThrow(guard::validar);
    }

    @Test
    void sinSemillaNoSeExigeCambiarLasClavesDePrueba() {
        ProduccionGuard guard = new ProduccionGuard(JWT, CLAVE_AES, false, "1234", "1234", "https://app.example.com");
        assertTrue(guard.problemas().isEmpty());
    }

    @Test
    void unJwtCortoSeRechaza() {
        ProduccionGuard guard = new ProduccionGuard("corto", CLAVE_AES, false, "x", "x", "https://app.example.com");
        assertEquals(1, guard.problemas().size());
    }
}
