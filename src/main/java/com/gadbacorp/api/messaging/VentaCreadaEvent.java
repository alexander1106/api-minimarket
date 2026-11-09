package com.gadbacorp.api.messaging;

/** Evento de dominio publicado cuando se registra una venta (topic Kafka "venta-creada"). */
public record VentaCreadaEvent(
        Integer idVenta,
        String nroComprobante,
        String tipoComprobante,
        Double total,
        Integer idCliente,
        Integer idSucursal,
        String fecha) {
}
