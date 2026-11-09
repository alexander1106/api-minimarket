package com.gadbacorp.reportes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Mismo contrato que VentaCreadaEvent de api-minimarket. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VentaEvent(
        Integer idVenta,
        String nroComprobante,
        String tipoComprobante,
        Double total,
        Integer idCliente,
        Integer idSucursal,
        String fecha) {
}
