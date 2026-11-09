package com.gadbacorp.api.integracion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gadbacorp.api.messaging.VentaCreadaEvent;
import com.gadbacorp.api.messaging.VentaEventPublisher;

/** Publica un evento con el publicador real contra un broker Kafka real (contenedor) y lo consume. */
@Tag("integracion")
@Testcontainers
@SpringBootTest
class KafkaRealIT {

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("apache/kafka:3.8.0"));

    @DynamicPropertySource
    static void propiedades(DynamicPropertyRegistry registry) {
        registry.add("app.kafka.enabled", () -> "true");
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @Autowired
    VentaEventPublisher publisher;

    @Test
    void elEventoDeVentaLlegaAlTopicYPuedeLeerseComoJson() throws Exception {
        VentaCreadaEvent evento = new VentaCreadaEvent(
                777, "B-000777", "BOLETA", 59.9, 3, 1, "2026-10-17");

        publisher.publicarVentaCreada(evento);

        Map<String, Object> config = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "it-" + UUID.randomUUID(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config)) {
            consumer.subscribe(List.of("venta-creada"));
            ConsumerRecord<String, String> recibido = null;
            long limite = System.currentTimeMillis() + 30_000;
            while (recibido == null && System.currentTimeMillis() < limite) {
                for (ConsumerRecord<String, String> r : consumer.poll(Duration.ofMillis(500))) {
                    recibido = r;
                }
            }
            assertFalse(recibido == null, "no llego ningun mensaje al topic venta-creada");
            assertEquals("777", recibido.key());

            JsonNode json = new ObjectMapper().readTree(recibido.value());
            assertEquals(777, json.get("idVenta").asInt());
            assertEquals("B-000777", json.get("nroComprobante").asText());
            assertEquals(59.9, json.get("total").asDouble());
            assertEquals(1, json.get("idSucursal").asInt());
        }
    }
}
