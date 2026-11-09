package com.gadbacorp.api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtUtilTest {

    private static final String SECRET = "test-secret-test-secret-test-secret-0123456789";

    private final JwtUtil jwtUtil = new JwtUtil(SECRET, 60_000);

    @Test
    void generaYValidaToken() {
        UserDetails user = User.withUsername("ana").password("x").authorities("ADMIN").build();
        String token = jwtUtil.generateTokenUsua(user);

        assertEquals("ana", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, user));
        assertTrue(jwtUtil.validarToken(token));
    }

    @Test
    void rechazaTokenFirmadoConOtraClave() {
        String token = new JwtUtil("otra-clave-otra-clave-otra-clave-0123456789", 60_000).generarToken("ana");
        assertFalse(jwtUtil.validarToken(token));
    }

    @Test
    void rechazaTokenExpirado() {
        String token = new JwtUtil(SECRET, -1_000).generarToken("ana");
        assertFalse(jwtUtil.validarToken(token));
    }

    @Test
    void exigeSecretoDeAlMenos32Caracteres() {
        assertThrows(IllegalStateException.class, () -> new JwtUtil("corto", 1000));
    }
}
