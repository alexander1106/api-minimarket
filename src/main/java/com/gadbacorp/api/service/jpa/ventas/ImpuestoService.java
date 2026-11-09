package com.gadbacorp.api.service.jpa.ventas;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Desglose del IGV de una venta. En el comercio minorista los precios ya incluyen IGV: a cada linea afecta se le
 * separa la base imponible (precio / (1 + tasa)) y el impuesto es la diferencia. Las lineas inafectas no pagan IGV.
 */
@Service
public class ImpuestoService {

    private final BigDecimal tasa;

    public ImpuestoService(@Value("${app.igv.tasa:0.18}") BigDecimal tasa) {
        this.tasa = tasa;
    }

    public BigDecimal tasa() {
        return tasa;
    }

    /** Acumula los importes (con IGV incluido) de las lineas de un comprobante. */
    public Desglose nuevo() {
        return new Desglose();
    }

    public class Desglose {
        private BigDecimal afecto = BigDecimal.ZERO;
        private BigDecimal inafecto = BigDecimal.ZERO;

        /** @param importe subtotal de la linea con IGV incluido (si es afecta) */
        public Desglose agregar(double importe, boolean afectoIgv) {
            BigDecimal valor = BigDecimal.valueOf(importe);
            if (afectoIgv) {
                afecto = afecto.add(valor);
            } else {
                inafecto = inafecto.add(valor);
            }
            return this;
        }

        /** Operacion gravada: la base sobre la que se calcula el IGV. */
        public BigDecimal opGravada() {
            return afecto.divide(BigDecimal.ONE.add(tasa), 2, RoundingMode.HALF_UP);
        }

        public BigDecimal opInafecta() {
            return inafecto.setScale(2, RoundingMode.HALF_UP);
        }

        /** IGV = importe afecto - base: asi base + IGV siempre suma exactamente lo cobrado. */
        public BigDecimal igv() {
            return afecto.setScale(2, RoundingMode.HALF_UP).subtract(opGravada());
        }
    }
}
