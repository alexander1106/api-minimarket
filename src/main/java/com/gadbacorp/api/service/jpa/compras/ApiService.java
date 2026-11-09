package com.gadbacorp.api.service.jpa.compras;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

/** Consulta RUC en SUNAT (apis.net.pe) de forma reactiva y no bloqueante. */
@Service
public class ApiService {

    /** API pública y gratuita (sin token, con límite de uso): se usa cuando no hay token configurado. */
    private static final String URL_GRATUITA = "https://api.apis.net.pe/v1";

    private final WebClient web;
    private final WebClient webGratuita;
    private final String token;

    public ApiService(WebClient.Builder webClientBuilder,
                      @Value("${apis.net.pe.base-url:https://api.apis.net.pe/v2}") String baseUrl,
                      @Value("${apis.net.pe.token-ruc:}") String token) {
        this.web = webClientBuilder.clone().baseUrl(baseUrl).build();
        this.webGratuita = webClientBuilder.clone().baseUrl(URL_GRATUITA).build();
        this.token = token;
    }

    public Mono<String> consumirApi(String ruc) {
        if (token == null || token.isBlank()) {
            return webGratuita.get()
                    .uri(uri -> uri.path("/ruc").queryParam("numero", ruc).build())
                    .retrieve()
                    .bodyToMono(String.class);
        }
        return web.get()
                .uri(uri -> uri.path("/sunat/ruc/full").queryParam("numero", ruc).build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class);
    }
}
