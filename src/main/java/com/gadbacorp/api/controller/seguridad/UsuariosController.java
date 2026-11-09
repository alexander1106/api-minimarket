package com.gadbacorp.api.controller.seguridad;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.config.JwtUtil;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.empleados.UsuariosDTO;
import com.gadbacorp.api.entity.seguridad.Rol;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.repository.seguridad.RolRepository;
import com.gadbacorp.api.service.empleados.IUsuariosService;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Seguridad - Usuarios")
@RestController
@RequestMapping("/api/minimarket")
public class UsuariosController {

    @Autowired
    private IUsuariosService usuariosService;

    @Autowired
    private com.gadbacorp.api.service.jpa.administrable.EmpresaResumenService empresaResumenService;

    @Autowired
    private SucursalesRepository sucursalesRepository;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    @GetMapping("/usuarios")
    public List<Usuarios> buscarTodos() {
        return usuariosService.buscarTodos();
    }

    @GetMapping("/usuarios/logged")
public ResponseEntity<UsuariosDTO> getLoggedUser(Principal principal) {
    Optional<Usuarios> opt = usuariosService.buscarPorUsername(principal.getName());
    if (opt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    Usuarios u = opt.get();
    UsuariosDTO dto = new UsuariosDTO();
    dto.setIdUsuario(u.getIdUsuario());
    dto.setNombre(u.getNombre());
    dto.setUsername(u.getUsername());
    dto.setApellidos(u.getApellidos());
    dto.setEmail(u.getEmail());
    dto.setDni(u.getDni());
    dto.setTurno(u.getTurno());
    dto.setEstado(u.getEstado());
    dto.setIdEmpresa(u.getSucursal() != null 
        ? u.getSucursal().getEmpresa().getIdempresa() 
        : null);
    dto.setIdSucursal(u.getSucursal() != null 
        ? u.getSucursal().getIdSucursal() 
        : null);
    dto.setRolId(u.getRol() != null 
        ? u.getRol().getId() 
        : null);
    dto.setFechaCreacion(u.getFechaCreacion());
    return ResponseEntity.ok(dto);
}
    @PostMapping("/usuarios")
    public ResponseEntity<?> guardar(@RequestBody UsuariosDTO dto) {

        if (dto.getRolId() == null) {
            return ResponseEntity.badRequest().body("El rol es obligatorio.");
        }
        Rol rol = rolRepository.findById(dto.getRolId()).orElse(null);
        if (rol == null) {
            return ResponseEntity.badRequest().body("rol no encontrado con ID: " + dto.getRolId());
        }

        boolean esSuperadmin = "SUPERADMIN".equalsIgnoreCase(rol.getNombre());
        if (esSuperadmin && !quienLlamaEsSuperadmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo un SUPERADMIN puede crear otro SUPERADMIN.");
        }

        // Los usuarios pertenecen a una sucursal de una empresa; el SUPERADMIN (dueno de la plataforma) no
        Sucursales sucursales = null;
        if (!esSuperadmin) {
            Integer idSucursal = dto.getIdSucursal();
            if (idSucursal == null && dto.getIdEmpresa() != null) {
                // se indico solo la empresa (p. ej. el SUPERADMIN creando al propietario): va a su primera sucursal
                idSucursal = sucursalesRepository.findByEmpresaIdempresa(dto.getIdEmpresa()).stream()
                        .map(Sucursales::getIdSucursal).sorted().findFirst().orElse(null);
                if (idSucursal == null) {
                    return ResponseEntity.badRequest().body("La empresa no tiene sucursales.");
                }
            }
            if (idSucursal == null) {
                return ResponseEntity.badRequest().body("La sucursal es obligatoria.");
            }
            dto.setIdSucursal(idSucursal);
            sucursales = sucursalesRepository.findById(dto.getIdSucursal()).orElse(null);
            if (sucursales == null) {
                return ResponseEntity.badRequest().body("sucursal no encontrado con ID: " + dto.getIdSucursal());
            }
            // el plan de la empresa limita cuantos usuarios puede tener
            empresaResumenService.verificarCupoUsuarios(sucursales.getEmpresa());
        }

        Usuarios usuarios= new Usuarios();
        usuarios.setNombre(dto.getNombre());
        usuarios.setUsername(dto.getUsername());
        usuarios.setApellidos(dto.getApellidos());
        usuarios.setEmail(dto.getEmail());
        usuarios.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuarios.setFechaCreacion(LocalDateTime.now());
        usuarios.setDni(dto.getDni());
        usuarios.setTurno(dto.getTurno());
        usuarios.setSucursal(sucursales);
        usuarios.setFechaCreacion(dto.getFechaCreacion());
        usuarios.setRol(rol);
        return ResponseEntity.ok(usuariosService.guardar(usuarios));
    }
    
    private boolean quienLlamaEsSuperadmin() {
        org.springframework.security.core.Authentication a =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return a != null && a.getAuthorities().stream().anyMatch(g -> "SUPERADMIN".equals(g.getAuthority()));
    }

    /**
     * Edita un usuario existente. Solo cambia los campos que llegan: sin password se conserva la actual,
     * y nadie que no sea SUPERADMIN puede asignar ese rol ni modificar a un SUPERADMIN.
     */
    @PutMapping("/usuarios")
    public ResponseEntity<?> modificar(@RequestBody UsuariosDTO dto) {
        if (dto.getIdUsuario() == null) {
            return ResponseEntity.badRequest().body("El id del usuario es obligatorio.");
        }
        Usuarios usuario = usuariosService.buscarId(dto.getIdUsuario()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }
        boolean llamaSuperadmin = quienLlamaEsSuperadmin();
        if (!llamaSuperadmin && usuario.getRol() != null && "SUPERADMIN".equalsIgnoreCase(usuario.getRol().getNombre())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo un SUPERADMIN puede modificar a un SUPERADMIN.");
        }

        if (dto.getRolId() != null) {
            Rol rol = rolRepository.findById(dto.getRolId()).orElse(null);
            if (rol == null) {
                return ResponseEntity.badRequest().body("rol no encontrado con ID: " + dto.getRolId());
            }
            if (!llamaSuperadmin && "SUPERADMIN".equalsIgnoreCase(rol.getNombre())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo un SUPERADMIN puede asignar el rol SUPERADMIN.");
            }
            usuario.setRol(rol);
        }
        if (dto.getIdSucursal() != null) {
            Sucursales sucursal = sucursalesRepository.findById(dto.getIdSucursal()).orElse(null);
            if (sucursal == null) {
                return ResponseEntity.badRequest().body("sucursal no encontrado con ID: " + dto.getIdSucursal());
            }
            usuario.setSucursal(sucursal);
        }

        if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());
        if (dto.getApellidos() != null) usuario.setApellidos(dto.getApellidos());
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) usuario.setUsername(dto.getUsername());
        if (dto.getEmail() != null) usuario.setEmail(dto.getEmail());
        if (dto.getDni() != null) usuario.setDni(dto.getDni());
        if (dto.getTurno() != null) usuario.setTurno(dto.getTurno());
        if (dto.getEstado() != null) usuario.setEstado(dto.getEstado());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return ResponseEntity.ok(usuariosService.modificar(usuario));
    }


    @GetMapping("/usuarios/{id}")
    public Optional<Usuarios> buscarId(@PathVariable("id") Integer id){
        return usuariosService.buscarId(id);
    }

    @DeleteMapping("/usuarios/{id}")
    public String eliminar(@PathVariable Integer id){
        usuariosService.buscarId(id).ifPresent(u -> {
            if (u.getRol() != null && "SUPERADMIN".equalsIgnoreCase(u.getRol().getNombre()) && !quienLlamaEsSuperadmin()) {
                throw new org.springframework.web.server.ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Solo un SUPERADMIN puede eliminar a un SUPERADMIN.");
            }
        });
        usuariosService.eliminar(id);
        return "Usuario elimnado";
    }
}