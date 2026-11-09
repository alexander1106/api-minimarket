package com.gadbacorp.api.entity.administrable;

/**
 * Cuerpo JSON del formulario "Registrar Nueva Empresa / Minimarket" (POST /empresas) y de su edicion
 * (PUT /empresas/{id}, donde todos los campos son opcionales y solo se actualiza lo que llega).
 *
 * <ul>
 *   <li>1. Identificacion: nombreComercial*, ruc* (11 digitos), razonsocial (razon social SUNAT; si falta, se usa el nombre comercial)</li>
 *   <li>2. Propietario: propietario*, correo*, telefono*</li>
 *   <li>3. Ubicacion: direccion*, distrito, ciudad, departamento, sucursales (cuantas crear, minimo 1)</li>
 *   <li>4. Suscripcion: idPlan, ciclo (MENSUAL|ANUAL), estadoInicial (ACTIVO|PRUEBA|SUSPENDIDO),
 *       facturacionElectronica, notas, fechaVencimiento (opcional: si falta se calcula)</li>
 * </ul>
 */
public record RegistroEmpresaRequest(
        String nombreComercial,
        String ruc,
        String razonsocial,
        String propietario,
        String correo,
        String telefono,
        String direccion,
        String distrito,
        String ciudad,
        String departamento,
        Integer sucursales,
        Integer idPlan,
        String ciclo,
        String estadoInicial,
        Boolean facturacionElectronica,
        String notas,
        String fechaVencimiento) {
}
