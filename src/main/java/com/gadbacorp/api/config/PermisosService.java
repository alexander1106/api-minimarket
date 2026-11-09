package com.gadbacorp.api.config;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.repository.seguridad.RolModuloRepository;

/**
 * Fuente unica de permisos: la tabla rol_modulo. Indica que modulos del menu (Ventas, Caja, ...) tiene cada rol.
 * Se usa tanto para armar el menu del frontend como para autorizar las operaciones de escritura.
 * El resultado se guarda en memoria unos segundos para no consultar la base en cada peticion.
 */
@Service
public class PermisosService {

    private record Entrada(Set<String> modulos, long vence) {
    }

    private final RolModuloRepository rolModuloRepository;
    private final long ttlMs;
    private final Map<String, Entrada> cache = new ConcurrentHashMap<>();

    public PermisosService(RolModuloRepository rolModuloRepository,
                           @Value("${app.security.permisos-cache-segundos:30}") long ttlSegundos) {
        this.rolModuloRepository = rolModuloRepository;
        this.ttlMs = ttlSegundos * 1000;
    }

    /** Nombres de los modulos asignados al rol (sin importar mayusculas). */
    public Set<String> modulosDelRol(String rol) {
        if (rol == null || rol.isBlank()) {
            return Set.of();
        }
        String clave = rol.trim().toUpperCase(Locale.ROOT);
        long ahora = System.currentTimeMillis();
        Entrada entrada = cache.get(clave);
        if (entrada == null || entrada.vence() < ahora) {
            Set<String> modulos = rolModuloRepository.nombresDeModulosPorRol(clave).stream()
                    .collect(Collectors.toUnmodifiableSet());
            entrada = new Entrada(modulos, ahora + ttlMs);
            cache.put(clave, entrada);
        }
        return entrada.modulos();
    }

    public boolean rolTieneModulo(String rol, String modulo) {
        return modulosDelRol(rol).stream().anyMatch(m -> m.equalsIgnoreCase(modulo));
    }

    /** Modulos del rol ordenados alfabeticamente, para devolverlos al frontend. */
    public List<String> listarModulos(String rol) {
        return modulosDelRol(rol).stream().sorted().toList();
    }

    /** Descarta lo guardado (p. ej. despues de cambiar permisos). */
    public void limpiar() {
        cache.clear();
    }
}
