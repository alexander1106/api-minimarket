package com.gadbacorp.api.entity.seguridad;

import java.util.List;

/**
 * Fila de la pantalla de roles: los campos de siempre (id, nombre, estado) mas lo que hace falta para la matriz
 * de permisos. Es un superconjunto del JSON anterior del rol.
 */
public record RolResumenDTO(
        Integer id,
        String nombre,
        Integer estado,
        /** true = rol base del sistema: no se puede renombrar ni eliminar. */
        boolean sistema,
        long cantidadUsuarios,
        List<ModuloInfo> modulos) {

    public record ModuloInfo(Integer idModulo, String nombre) {
    }

    /** Cuerpo de POST /roles y PUT /roles. En PUT, solo se aplica lo que llega. {@code modulos} = ids que reemplazan los actuales. */
    public record RolRequest(Integer id, String nombre, List<Integer> modulos) {
    }

    /** Cuerpo de PUT /roles/{id}/modulos. */
    public record ModulosRequest(List<Integer> modulos) {
    }
}
