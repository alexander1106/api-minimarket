package com.gadbacorp.api.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Con el perfil prod la aplicacion NO arranca si quedaron valores de desarrollo: clave JWT por defecto, sin clave de cifrado,
 * usuarios de prueba con la contrasena 1234 o CORS abierto a cualquier origen.
 */
@Component
@Profile("prod")
public class ProduccionGuard {

    static final String JWT_DESARROLLO = "dev-only-change-me-dev-only-change-me-0123456789";

    private final String jwtSecret;
    private final String cryptoKey;
    private final boolean semilla;
    private final String claveAdmin;
    private final String claveEmpresa;
    private final String cors;

    public ProduccionGuard(@Value("${jwt.secret}") String jwtSecret,
                           @Value("${app.crypto.key:}") String cryptoKey,
                           @Value("${app.seed.enabled:true}") boolean semilla,
                           @Value("${app.seed.admin-password:1234}") String claveAdmin,
                           @Value("${app.seed.empresa-password:1234}") String claveEmpresa,
                           @Value("${app.cors.allowed-origins:*}") String cors) {
        this.jwtSecret = jwtSecret;
        this.cryptoKey = cryptoKey;
        this.semilla = semilla;
        this.claveAdmin = claveAdmin;
        this.claveEmpresa = claveEmpresa;
        this.cors = cors;
    }

    @PostConstruct
    public void validar() {
        List<String> problemas = problemas();
        if (!problemas.isEmpty()) {
            throw new IllegalStateException("Configuracion insegura para produccion:\n - " + String.join("\n - ", problemas));
        }
    }

    List<String> problemas() {
        List<String> p = new ArrayList<>();
        if (jwtSecret == null || jwtSecret.length() < 32 || JWT_DESARROLLO.equals(jwtSecret)) {
            p.add("JWT_SECRET debe definirse con un valor propio de al menos 32 caracteres");
        }
        if (cryptoKey == null || cryptoKey.isBlank()) {
            p.add("APP_CRYPTO_KEY debe definirse (32 bytes en Base64)");
        }
        if (semilla && ("1234".equals(claveAdmin) || "1234".equals(claveEmpresa))) {
            p.add("APP_SEED_ADMIN_PASSWORD y APP_SEED_EMPRESA_PASSWORD no pueden ser 1234");
        }
        if (cors == null || cors.isBlank() || cors.contains("*")) {
            p.add("CORS_ALLOWED_ORIGINS debe ser la URL exacta del frontend, no *");
        }
        return p;
    }
}
