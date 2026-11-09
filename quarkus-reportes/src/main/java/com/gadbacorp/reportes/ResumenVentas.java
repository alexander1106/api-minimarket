package com.gadbacorp.reportes;

import java.util.Map;
import java.util.TreeMap;

import jakarta.enterprise.context.ApplicationScoped;

/** Acumulador en memoria de las ventas recibidas por Kafka (suficiente para el reporte en vivo). */
@ApplicationScoped
public class ResumenVentas {

    private long cantidad;
    private double montoTotal;
    private final Map<String, Double> montoPorComprobante = new TreeMap<>();

    public synchronized void registrar(VentaEvent venta) {
        cantidad++;
        double total = venta.total() == null ? 0 : venta.total();
        montoTotal += total;
        String tipo = venta.tipoComprobante() == null ? "SIN_TIPO" : venta.tipoComprobante().toUpperCase();
        montoPorComprobante.merge(tipo, total, Double::sum);
    }

    public synchronized Resumen snapshot() {
        return new Resumen(cantidad, montoTotal, Map.copyOf(montoPorComprobante));
    }

    public record Resumen(long cantidadVentas, double montoTotal, Map<String, Double> montoPorComprobante) {
    }
}
