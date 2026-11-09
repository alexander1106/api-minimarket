package com.gadbacorp.api.config;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Roles y modulos que el sistema da por hechos (el menu del frontend y las reglas de SecurityConfig dependen de ellos).
 * No se pueden renombrar ni eliminar; si, en cambio, se pueden crear roles nuevos y repartirles modulos.
 */
public final class SeguridadCatalogo {

    public static final String SUPERADMIN = "SUPERADMIN";

    public static final List<String> ROLES_SISTEMA =
            List.of(SUPERADMIN, "ADMIN", "SUPERVISOR", "CAJERO", "ALMACENERO", "EMPRESA");

    public static final List<String> MODULOS_SISTEMA = List.of("Administrable", "Dashboard", "Seguridad", "Inventario",
            "Almacenes", "Productos", "Compras", "Ventas", "Delivery", "Caja", "Reportes");

    /** El dueno de la plataforma solo puede tener estos modulos y siempre debe conservar el primero. */
    public static final Set<String> MODULOS_DEL_SUPERADMIN = Set.of("Administrable", "Seguridad");
    public static final String MODULO_OBLIGATORIO_SUPERADMIN = "Administrable";

    private SeguridadCatalogo() {
    }

    public static boolean esRolDelSistema(String nombre) {
        return nombre != null && ROLES_SISTEMA.contains(nombre.trim().toUpperCase(Locale.ROOT));
    }

    public static boolean esModuloDelSistema(String nombre) {
        return nombre != null && MODULOS_SISTEMA.stream().anyMatch(m -> m.equalsIgnoreCase(nombre.trim()));
    }
}
