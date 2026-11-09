package com.gadbacorp.reportes;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VentasConsumer {

    private static final Logger LOG = Logger.getLogger(VentasConsumer.class);

    @Inject
    ObjectMapper mapper;

    @Inject
    ResumenVentas resumen;

    @Incoming("ventas")
    public void onVenta(String json) {
        try {
            resumen.registrar(mapper.readValue(json, VentaEvent.class));
        } catch (JsonProcessingException e) {
            LOG.errorf(e, "Evento de venta invalido, se descarta: %s", json);
        }
    }
}
