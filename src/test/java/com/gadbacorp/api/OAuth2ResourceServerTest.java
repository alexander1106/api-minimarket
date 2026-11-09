package com.gadbacorp.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * OAuth2 Resource Server activado: la API acepta tokens de un proveedor de identidad externo (aqui simulado con
 * una clave HS256 distinta) y sigue aceptando sus propios JWT. Con un IdP real (Keycloak, Auth0, Azure AD) basta
 * con definir OAUTH2_ISSUER_URI: el decodificador se construye solo a partir de su descubrimiento OIDC.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.oauth2.enabled=true")
class OAuth2ResourceServerTest {

    private static final String CLAVE_DEL_IDP = "clave-del-proveedor-de-identidad-externo-0123456789";

    @TestConfiguration
    static class IdpSimulado {
        @Bean
        JwtDecoder jwtDecoder() {
            return NimbusJwtDecoder
                    .withSecretKey(new SecretKeySpec(CLAVE_DEL_IDP.getBytes(StandardCharsets.UTF_8), "HmacSHA256"))
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();
        }
    }

    @Autowired
    MockMvc mockMvc;

    private String tokenDelIdp(String clave, long expiraEnMs) {
        return Jwts.builder()
                .setSubject("usuario-del-idp")
                .claim("scope", "read")
                .setExpiration(new Date(System.currentTimeMillis() + expiraEnMs))
                .signWith(Keys.hmacShaKeyFor(clave.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void aceptaUnTokenEmitidoPorElProveedorExterno() throws Exception {
        mockMvc.perform(get("/api/minimarket/roles")
                .header("Authorization", "Bearer " + tokenDelIdp(CLAVE_DEL_IDP, 60_000)))
                .andExpect(status().isOk());
    }

    @Test
    void rechazaUnTokenFirmadoConOtraClave() throws Exception {
        mockMvc.perform(get("/api/minimarket/roles")
                .header("Authorization", "Bearer " + tokenDelIdp("otra-clave-que-nadie-conoce-0123456789-abc", 60_000)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rechazaUnTokenExpirado() throws Exception {
        mockMvc.perform(get("/api/minimarket/roles")
                .header("Authorization", "Bearer " + tokenDelIdp(CLAVE_DEL_IDP, -60_000)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sigueAceptandoElJwtPropioDeLaAplicacion() throws Exception {
        String respuesta = mockMvc.perform(post("/api/minimarket/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alez\",\"password\":\"1234\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String token = respuesta.replaceAll("(?s).*\"token\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/minimarket/roles").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void unUsuarioDelIdpSinRolNoPuedeEscribir() throws Exception {
        // el IdP identifica al usuario, pero los permisos de escritura siguen saliendo de rol_modulo
        mockMvc.perform(post("/api/minimarket/ventas")
                .header("Authorization", "Bearer " + tokenDelIdp(CLAVE_DEL_IDP, 60_000))
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }
}
