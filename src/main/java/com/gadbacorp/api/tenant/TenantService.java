package com.gadbacorp.api.tenant;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.empleados.Usuarios;

/**
 * Empresa (tenant) del usuario autenticado. Cada empresa solo ve y modifica lo suyo.
 * SUPERADMIN (dueno de la plataforma) no tiene empresa y no se le restringe.
 * Un usuario sin sucursal que no es SUPERADMIN no ve nada de ninguna empresa.
 */
public final class TenantService {

    private TenantService() {
    }

    /** Empresa que no existe: un usuario sin sucursal queda aislado de todo. */
    private static final int SIN_EMPRESA = -1;

    /** @return id de la empresa del usuario, o null si no se debe restringir (SUPERADMIN, procesos internos) */
    public static Integer empresaActual() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated() || a instanceof AnonymousAuthenticationToken
                || !(a.getPrincipal() instanceof Usuarios usuario)) {
            return null;
        }
        if (usuario.getRol() != null && "SUPERADMIN".equals(usuario.getRol().getNombre())) {
            return null;
        }
        Integer empresa = empresaDe(usuario.getSucursal());
        return empresa != null ? empresa : SIN_EMPRESA;
    }

    public static boolean restringido() {
        return empresaActual() != null;
    }

    /** true si el recurso (identificado por el id de su empresa) lo puede ver el usuario actual. */
    public static boolean puedeVer(Integer idEmpresaDelRecurso) {
        Integer actual = empresaActual();
        return actual == null || actual.equals(idEmpresaDelRecurso);
    }

    /** @throws java.util.NoSuchElementException (404) si el recurso es de otra empresa: no se revela que existe */
    public static void exigir(Integer idEmpresaDelRecurso) {
        if (!puedeVer(idEmpresaDelRecurso)) {
            throw new java.util.NoSuchElementException("Recurso no encontrado");
        }
    }

    public static Integer empresaDe(Sucursales sucursal) {
        return sucursal == null ? null : empresaDe(sucursal.getEmpresa());
    }

    public static Integer empresaDe(Empresas empresa) {
        return empresa == null ? null : empresa.getIdempresa();
    }
}
