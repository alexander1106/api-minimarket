package com.gadbacorp.api.entity.administrable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Fila del panel de suscripciones (GET /empresas): los campos planos de siempre mas el resumen comercial.
 * Es un superconjunto del JSON anterior de la empresa, de modo que las pantallas existentes siguen funcionando.
 */
public record EmpresaResumenDTO(
        Integer idempresa,
        /** Nombre comercial; si la empresa es anterior a este campo, se usa la razon social. */
        String nombreComercial,
        String razonsocial,
        String ruc,
        String ciudad,
        String direccion,
        String correo,
        LocalDate fechaRegistro,
        String logo,
        Integer estado,
        long cantidadSucursales,
        boolean sunatVerificado,
        boolean facturacionElectronica,
        String notas,
        Propietario propietario,
        Ubicacion ubicacion,
        PlanInfo plan,
        /** Ingreso recurrente mensual (MRR). 0 si esta en prueba, vencida o suspendida. */
        BigDecimal ingresoMensual,
        String moneda,
        LocalDate vencimiento,
        LocalDate fechaAlta,
        UsuariosInfo usuarios,
        EstadoSuscripcion estadoSuscripcion) {

    public record Propietario(String nombre, String telefono) {
    }

    public record Ubicacion(String ciudad, String distrito, String departamento) {
    }

    public record PlanInfo(Integer idPlan, String nombre, String ciclo, BigDecimal precioMensual) {
    }

    /** limite nulo = el plan no limita usuarios. */
    public record UsuariosInfo(long usados, Integer limite) {
    }

    /** codigo: ACTIVO, PRUEBA, VENCIDO o SUSPENDIDO. diasRestantes: hasta el vencimiento (negativo si ya vencio). */
    public record EstadoSuscripcion(String codigo, String etiqueta, Long diasRestantes) {
    }
}
