package com.gadbacorp.api.service.jpa.ventas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

/** Consulta DNI en RENIEC (apis.net.pe) de forma reactiva y no bloqueante. */
@Service
public class ApiCliente {

    private final WebClient web;
    private final String token;

    public ApiCliente(WebClient.Builder webClientBuilder,
                      @Value("${apis.net.pe.base-url:https://api.apis.net.pe/v2}") String baseUrl,
                      @Value("${apis.net.pe.token-dni:}") String token) {
        this.web = webClientBuilder.baseUrl(baseUrl).build();
        this.token = token;
    }

    public Mono<String> consumirApi(String dni) {
        return web.get()
                .uri(uri -> uri.path("/reniec/dni").queryParam("numero", dni).build())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class);
    }
}
