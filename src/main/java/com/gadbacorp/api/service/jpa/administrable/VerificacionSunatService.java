package com.gadbacorp.api.service.jpa.administrable;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gadbacorp.api.service.jpa.compras.ApiService;

/** Confirma que un RUC existe en SUNAT (via apis.net.pe) y que su razon social coincide con la registrada. */
@Service
public class VerificacionSunatService {

    private final ApiService apiService;
    private final ObjectMapper mapper;
    private final boolean configurado;

    public VerificacionSunatService(ApiService apiService, ObjectMapper mapper,
                                    @Value("${apis.net.pe.token-ruc:}") String token) {
        this.apiService = apiService;
        this.mapper = mapper;
        this.configurado = token != null && !token.isBlank();
    }

    public boolean estaConfigurado() {
        return configurado;
    }

    /**
     * @return true si SUNAT conoce el RUC. La razon social registrada debe parecerse a la de SUNAT
     *         (se ignoran mayusculas, tildes y signos).
     * @throws IllegalArgumentException si el RUC no tiene 11 digitos
     * @throws IllegalStateException si la consulta a SUNAT falla
     */
    public boolean verificar(String ruc, String razonSocialRegistrada) {
        if (ruc == null || !ruc.matches("\\d{11}")) {
            throw new IllegalArgumentException("El RUC debe tener 11 digitos.");
        }
        String respuesta;
        try {
            respuesta = apiService.consumirApi(ruc).block(Duration.ofSeconds(15));
        } catch (RuntimeException e) {
            throw new IllegalStateException("No se pudo consultar a SUNAT: " + e.getMessage(), e);
        }
        if (respuesta == null || respuesta.isBlank()) {
            return false;
        }
        try {
            JsonNode json = mapper.readTree(respuesta);
            String sunat = json.path("razonSocial").asText("");
            return !sunat.isBlank() && normalizar(sunat).equals(normalizar(razonSocialRegistrada));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return false;
        }
    }

    static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9]", "")
                .toUpperCase();
    }
}
