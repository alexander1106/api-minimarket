package com.gadbacorp.api.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Publica los eventos de venta: siempre al canal reactivo interno (SSE) y,
 * si app.kafka.enabled=true, tambien al topic de Kafka. Si hay una transaccion
 * activa, la publicacion se hace despues del commit para no emitir ventas que
 * luego se reviertan.
 */
@Component
public class VentaEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(VentaEventPublisher.class);

    private final VentaEventStream stream;
    private final ObjectProvider<KafkaTemplate<String, String>> kafkaTemplate;
    private final ObjectMapper mapper;
    private final boolean kafkaEnabled;
    private final String topic;

    public VentaEventPublisher(VentaEventStream stream,
                               ObjectProvider<KafkaTemplate<String, String>> kafkaTemplate,
                               ObjectMapper mapper,
                               @Value("${app.kafka.enabled:false}") boolean kafkaEnabled,
                               @Value("${app.kafka.topic.venta-creada:venta-creada}") String topic) {
        this.stream = stream;
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
        this.kafkaEnabled = kafkaEnabled;
        this.topic = topic;
    }

    public void publicarVentaCreada(VentaCreadaEvent event) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    enviar(event);
                }
            });
        } else {
            enviar(event);
        }
    }

    private void enviar(VentaCreadaEvent event) {
        stream.emit(event);
        if (!kafkaEnabled) {
            return;
        }
        KafkaTemplate<String, String> template = kafkaTemplate.getIfAvailable();
        if (template == null) {
            return;
        }
        try {
            template.send(topic, String.valueOf(event.idVenta()), mapper.writeValueAsString(event));
        } catch (JsonProcessingException | RuntimeException e) {
            // La venta ya esta confirmada: un fallo de mensajeria no debe romper la operacion
            log.error("No se pudo publicar el evento de la venta {} en Kafka", event.idVenta(), e);
        }
    }
}
