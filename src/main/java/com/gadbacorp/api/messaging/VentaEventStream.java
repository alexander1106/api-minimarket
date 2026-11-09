package com.gadbacorp.api.messaging;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Canal reactivo (Reactor/WebFlux) con los eventos de venta en tiempo real.
 * Se expone como Server-Sent Events en GET /api/minimarket/ventas/eventos.
 */
@Component
public class VentaEventStream {

    private final Sinks.Many<VentaCreadaEvent> sink = Sinks.many().multicast().directBestEffort();

    public void emit(VentaCreadaEvent event) {
        sink.tryEmitNext(event);
    }

    public Flux<VentaCreadaEvent> flux() {
        return sink.asFlux();
    }
}
