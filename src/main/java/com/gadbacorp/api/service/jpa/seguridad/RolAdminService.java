package com.gadbacorp.api.service.jpa.seguridad;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.config.PermisosService;
import com.gadbacorp.api.config.SeguridadCatalogo;
import com.gadbacorp.api.entity.seguridad.Modulo;
import com.gadbacorp.api.entity.seguridad.Rol;
import com.gadbacorp.api.entity.seguridad.RolModulo;
import com.gadbacorp.api.entity.seguridad.RolResumenDTO;
import com.gadbacorp.api.entity.seguridad.RolResumenDTO.ModuloInfo;
import com.gadbacorp.api.entity.seguridad.RolResumenDTO.RolRequest;
import com.gadbacorp.api.excepciones.OperacionNoPermitidaException;
import com.gadbacorp.api.excepciones.RecursoDuplicadoException;
import com.gadbacorp.api.excepciones.RegistroInvalidoException;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.seguridad.ModuloRepository;
import com.gadbacorp.api.repository.seguridad.RolModuloRepository;
import com.gadbacorp.api.repository.seguridad.RolRepository;

/**
 * Administracion de roles y de sus modulos (permisos). rol_modulo es la fuente de permisos del sistema: lo que se
 * guarda aqui define el menu del frontend y quien puede escribir en cada modulo, y se aplica de inmediato.
 */
@Service
public class RolAdminService {

    private final RolRepository rolRepository;
    private final ModuloRepository moduloRepository;
    private final RolModuloRepository rolModuloRepository;
    private final UsuarioRepository usuarioRepository;
    private final PermisosService permisosService;

    public RolAdminService(RolRepository rolRepository, ModuloRepository moduloRepository,
                           RolModuloRepository rolModuloRepository, UsuarioRepository usuarioRepository,
                           PermisosService permisosService) {
        this.rolRepository = rolRepository;
        this.moduloRepository = moduloRepository;
        this.rolModuloRepository = rolModuloRepository;
        this.usuarioRepository = usuarioRepository;
        this.permisosService = permisosService;
    }

    @Transactional(readOnly = true)
    public List<RolResumenDTO> listar() {
        Map<Integer, List<ModuloInfo>> modulosPorRol = new HashMap<>();
        for (RolModulo rm : rolModuloRepository.findAll()) {
            if (rm.getRol() != null && rm.getModulo() != null) {
                modulosPorRol.computeIfAbsent(rm.getRol().getId(), k -> new ArrayList<>())
                        .add(new ModuloInfo(rm.getModulo().getIdModulo(), rm.getModulo().getNombre()));
            }
        }
        Map<Integer, Long> usuariosPorRol = new HashMap<>();
        for (Object[] fila : usuarioRepository.contarPorRol()) {
            usuariosPorRol.put(((Number) fila[0]).intValue(), ((Number) fila[1]).longValue());
        }
        return rolRepository.findAll().stream()
                .sorted(Comparator.comparing(Rol::getId))
                .map(r -> resumir(r, modulosPorRol.getOrDefault(r.getId(), List.of()), usuariosPorRol.getOrDefault(r.getId(), 0L)))
                .toList();
    }

    @Transactional(readOnly = true)
    public RolResumenDTO obtener(Integer id) {
        Rol rol = buscar(id);
        return resumir(rol, modulosDe(id), usuarioRepository.countByRolId(id));
    }

    /** Roles que quien llama puede asignar al crear un usuario: solo un SUPERADMIN puede asignar SUPERADMIN. */
    @Transactional(readOnly = true)
    public List<RolResumenDTO> asignables(boolean quienLlamaEsSuperadmin) {
        return listar().stream()
                .filter(r -> quienLlamaEsSuperadmin || !SeguridadCatalogo.SUPERADMIN.equalsIgnoreCase(r.nombre()))
                .toList();
    }

    @Transactional
    public RolResumenDTO crear(RolRequest req) {
        String nombre = normalizarNombre(req.nombre());
        validarNombre(nombre);
        if (rolRepository.findByNombreIgnoreCase(nombre).isPresent()) {
            throw new RecursoDuplicadoException("Ya existe un rol llamado " + nombre + ".");
        }
        Rol rol = new Rol();
        rol.setNombre(nombre);
        rol.setEstado(1);
        rol = rolRepository.save(rol);
        if (req.modulos() != null) {
            aplicarModulos(rol, req.modulos());
        }
        return obtener(rol.getId());
    }

    @Transactional
    public RolResumenDTO actualizar(RolRequest req) {
        if (req.id() == null) {
            throw new RegistroInvalidoException(Map.of("id", "El id del rol es obligatorio."));
        }
        Rol rol = buscar(req.id());
        if (req.nombre() != null) {
            String nombre = normalizarNombre(req.nombre());
            if (!nombre.equals(rol.getNombre())) {
                if (SeguridadCatalogo.esRolDelSistema(rol.getNombre())) {
                    throw new OperacionNoPermitidaException(
                            "El rol " + rol.getNombre() + " es del sistema y no puede cambiar de nombre.");
                }
                validarNombre(nombre);
                if (rolRepository.findByNombreIgnoreCase(nombre).filter(o -> !o.getId().equals(rol.getId())).isPresent()) {
                    throw new RecursoDuplicadoException("Ya existe un rol llamado " + nombre + ".");
                }
                rol.setNombre(nombre);
                rolRepository.save(rol);
            }
        }
        if (req.modulos() != null) {
            aplicarModulos(rol, req.modulos());
        }
        permisosService.limpiar();
        return obtener(rol.getId());
    }

    /** Reemplaza los modulos del rol por exactamente los indicados (la matriz de checkboxes se guarda de una vez). */
    @Transactional
    public RolResumenDTO asignarModulos(Integer idRol, List<Integer> idsModulos) {
        Rol rol = buscar(idRol);
        aplicarModulos(rol, idsModulos == null ? List.of() : idsModulos);
        return obtener(idRol);
    }

    @Transactional
    public void eliminar(Integer id) {
        Rol rol = buscar(id);
        if (SeguridadCatalogo.esRolDelSistema(rol.getNombre())) {
            throw new OperacionNoPermitidaException("El rol " + rol.getNombre() + " es del sistema y no se puede eliminar.");
        }
        long usuarios = usuarioRepository.countByRolId(id);
        if (usuarios > 0) {
            throw new OperacionNoPermitidaException("No se puede eliminar el rol " + rol.getNombre() + ": lo tienen "
                    + usuarios + " usuario(s). Reasígnalos primero.");
        }
        rolModuloRepository.findAll().stream()
                .filter(rm -> rm.getRol() != null && id.equals(rm.getRol().getId()))
                .forEach(rolModuloRepository::delete);
        rolRepository.deleteById(id);
        permisosService.limpiar();
    }

    private void aplicarModulos(Rol rol, List<Integer> idsPedidos) {
        Set<Integer> ids = new HashSet<>(idsPedidos);
        List<Modulo> modulos = moduloRepository.findAllById(ids);
        if (modulos.size() != ids.size()) {
            throw new RegistroInvalidoException(Map.of("modulos", "Alguno de los módulos indicados no existe."));
        }
        if (SeguridadCatalogo.SUPERADMIN.equalsIgnoreCase(rol.getNombre())) {
            Set<String> nombres = modulos.stream().map(Modulo::getNombre).collect(Collectors.toSet());
            if (!SeguridadCatalogo.MODULOS_DEL_SUPERADMIN.containsAll(nombres)) {
                throw new RegistroInvalidoException(Map.of("modulos",
                        "El SUPERADMIN solo puede tener los módulos " + SeguridadCatalogo.MODULOS_DEL_SUPERADMIN + "."));
            }
            if (!nombres.contains(SeguridadCatalogo.MODULO_OBLIGATORIO_SUPERADMIN)) {
                throw new RegistroInvalidoException(Map.of("modulos",
                        "El SUPERADMIN debe conservar el módulo " + SeguridadCatalogo.MODULO_OBLIGATORIO_SUPERADMIN + "."));
            }
        }

        List<RolModulo> actuales = rolModuloRepository.findAll().stream()
                .filter(rm -> rm.getRol() != null && rm.getModulo() != null && rol.getId().equals(rm.getRol().getId()))
                .toList();
        Set<Integer> yaAsignados = new HashSet<>();
        for (RolModulo rm : actuales) {
            Integer idModulo = rm.getModulo().getIdModulo();
            if (!ids.contains(idModulo) || !yaAsignados.add(idModulo)) {
                rolModuloRepository.delete(rm); // sobra o esta repetido
            }
        }
        for (Modulo m : modulos) {
            if (!yaAsignados.contains(m.getIdModulo())) {
                RolModulo nuevo = new RolModulo();
                nuevo.setRol(rol);
                nuevo.setModulo(m);
                nuevo.setEstado(1);
                rolModuloRepository.save(nuevo);
            }
        }
        rolModuloRepository.flush();
        permisosService.limpiar();
    }

    private List<ModuloInfo> modulosDe(Integer idRol) {
        return rolModuloRepository.findAll().stream()
                .filter(rm -> rm.getRol() != null && rm.getModulo() != null && idRol.equals(rm.getRol().getId()))
                .map(rm -> new ModuloInfo(rm.getModulo().getIdModulo(), rm.getModulo().getNombre()))
                .toList();
    }

    private static RolResumenDTO resumir(Rol rol, List<ModuloInfo> modulos, long usuarios) {
        List<ModuloInfo> ordenados = modulos.stream().distinct().sorted(Comparator.comparing(ModuloInfo::idModulo)).toList();
        return new RolResumenDTO(rol.getId(), rol.getNombre(), rol.getEstado(),
                SeguridadCatalogo.esRolDelSistema(rol.getNombre()), usuarios, ordenados);
    }

    private Rol buscar(Integer id) {
        return rolRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Rol no encontrado con ID: " + id));
    }

    /** Mayusculas, sin tildes y con guiones bajos en lugar de espacios: "Jefe de tienda" -> JEFE_DE_TIENDA. */
    static String normalizarNombre(String nombre) {
        if (nombre == null) {
            return "";
        }
        return Normalizer.normalize(nombre.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", "_")
                .toUpperCase(Locale.ROOT);
    }

    private static void validarNombre(String nombre) {
        Map<String, String> errores = new LinkedHashMap<>();
        if (nombre.isEmpty()) {
            errores.put("nombre", "El nombre del rol es obligatorio.");
        } else if (!nombre.matches("[A-Z0-9_]{3,30}")) {
            errores.put("nombre", "El nombre debe tener entre 3 y 30 caracteres: letras, números y guion bajo.");
        }
        if (!errores.isEmpty()) {
            throw new RegistroInvalidoException(errores);
        }
    }
}
