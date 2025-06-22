package com.gadbacorp.api.service.jpa.ventas;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class ApiCliente {

    private final WebClient web;
    private final String TOKEN = "apis-token-16182.k7zU3OrzDT3KDiO543Oi69rAoY8SloQQ";

    public ApiCliente(WebClient.Builder webClientBuilder) {
        this.web = webClientBuilder
            .baseUrl("https://api.apis.net.pe/v2")  // ← Dominio correcto
            .build();
    }

    public String consumirApi(String dni) {
        Mono<String> respuesta = web.get()
            .uri(uriBuilder -> uriBuilder
                .path("/reniec/dni")
                .queryParam("numero", dni)
                .build())
            .header("Authorization", "Bearer " + TOKEN)
            .retrieve()
            .bodyToMono(String.class);

        return respuesta.block(); // Devuelve la respuesta directamente
    }
}