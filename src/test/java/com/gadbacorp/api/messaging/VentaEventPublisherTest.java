package com.gadbacorp.api.messaging;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.test.StepVerifier;

class VentaEventPublisherTest {

    private final VentaCreadaEvent evento =
            new VentaCreadaEvent(7, "B-000007", "BOLETA", 50.0, 1, 2, "2026-01-01");

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, String> template = mock(KafkaTemplate.class);

    @SuppressWarnings("unchecked")
    private VentaEventPublisher publisher(VentaEventStream stream, boolean kafkaEnabled) {
        ObjectProvider<KafkaTemplate<String, String>> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(template);
        return new VentaEventPublisher(stream, provider, new ObjectMapper(), kafkaEnabled, "venta-creada");
    }

    @Test
    void envia_a_kafka_cuando_esta_habilitado() {
        publisher(new VentaEventStream(), true).publicarVentaCreada(evento);
        verify(template).send(eq("venta-creada"), eq("7"), anyString());
    }

    @Test
    void no_usa_kafka_cuando_esta_deshabilitado() {
        publisher(new VentaEventStream(), false).publicarVentaCreada(evento);
        verify(template, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void emite_en_el_stream_reactivo() {
        VentaEventStream stream = new VentaEventStream();
        VentaEventPublisher publisher = publisher(stream, false);

        StepVerifier.create(stream.flux().take(1))
                .then(() -> publisher.publicarVentaCreada(evento))
                .expectNext(evento)
                .verifyComplete();
    }

    @Test
    void un_fallo_de_kafka_no_rompe_la_venta() {
        when(template.send(anyString(), anyString(), anyString())).thenThrow(new RuntimeException("broker caido"));
        publisher(new VentaEventStream(), true).publicarVentaCreada(evento); // no debe lanzar
    }
}
