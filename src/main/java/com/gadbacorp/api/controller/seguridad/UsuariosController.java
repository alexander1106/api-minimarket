package com.gadbacorp.api.controller.seguridad;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.config.JwtUtil;
import com.gadbacorp.api.entity.administrable.Empresas;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.empleados.UsuariosDTO;
import com.gadbacorp.api.entity.seguridad.Rol;
import com.gadbacorp.api.repository.administrable.EmpresasRepository;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.repository.seguridad.RolRepository;
import com.gadbacorp.api.service.empleados.IUsuariosService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket")
public class UsuariosController {

    @Autowired
    private IUsuariosService usuariosService;

    @Autowired
    private SucursalesRepository sucursalesRepository;

    @Autowired
    private EmpresasRepository empresasRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    // ✅ Mostrar todos los usuarios con empresaNombre y rolId
    @GetMapping("/usuarios")
    public List<UsuariosDTO> buscarTodos() {
        return usuariosService.buscarTodos()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // ✅ Registro de usuario con validación de empresa, rol y sucursal opcional
    @PostMapping("/usuarios")
    public ResponseEntity<?> guardar(@RequestBody UsuariosDTO dto) {

        Sucursales sucursal = null;
        if (dto.getIdSucursal() != null && dto.getIdSucursal() > 0) {
            sucursal = sucursalesRepository.findById(dto.getIdSucursal()).orElse(null);
            if (sucursal == null) {
                return ResponseEntity.badRequest().body("Sucursal no encontrada con ID: " + dto.getIdSucursal());
            }
        }

        Empresas empresa = null;
        if (dto.getIdEmpresa() != null) {
            empresa = empresasRepository.findById(dto.getIdEmpresa()).orElse(null);
            if (empresa == null) {
                return ResponseEntity.badRequest().body("Empresa no encontrada con ID: " + dto.getIdEmpresa());
            }
        }

        Rol rol = rolRepository.findById(dto.getRolId()).orElse(null);
        if (rol == null) {
            return ResponseEntity.badRequest().body("Rol no encontrado con ID: " + dto.getRolId());
        }

        Usuarios usuarios = new Usuarios();
        usuarios.setNombre(dto.getNombre());
        usuarios.setUsername(dto.getUsername());
        usuarios.setApellidos(dto.getApellidos());
        usuarios.setEmail(dto.getEmail());
        usuarios.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuarios.setFechaCreacion(dto.getFechaCreacion());
        usuarios.setDni(dto.getDni());
        usuarios.setTurno(dto.getTurno());
        usuarios.setEstado(dto.getEstado());
        usuarios.setSucursal(sucursal); // puede ser null
        usuarios.setEmpresa(empresa);   // 👈 Asigna empresa
        usuarios.setRol(rol);

        return ResponseEntity.ok(usuariosService.guardar(usuarios));
    }

    @PutMapping("/usuarios")
    public Usuarios modificar(@RequestBody Usuarios empleado) {
        usuariosService.modificar(empleado);
        return empleado;
    }

    @GetMapping("/usuarios/{id}")
    public Optional<Usuarios> buscarId(@PathVariable("id") Integer id) {
        return usuariosService.buscarId(id);
    }

    @DeleteMapping("/usuarios/{id}")
    public String eliminar(@PathVariable Integer id) {
        usuariosService.eliminar(id);
        return "Usuario eliminado";
    }

    // ✅ Conversión de entidad a DTO con nombre de empresa incluido
    private UsuariosDTO toDTO(Usuarios u) {
        UsuariosDTO dto = new UsuariosDTO();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setNombre(u.getNombre());
        dto.setApellidos(u.getApellidos());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setDni(u.getDni());
        dto.setTurno(u.getTurno());
        dto.setEstado(u.getEstado());
        dto.setFechaCreacion(u.getFechaCreacion());

        if (u.getSucursal() != null) {
            dto.setIdSucursal(u.getSucursal().getIdSucursal());
        }

        if (u.getEmpresa() != null) {
            dto.setIdEmpresa(u.getEmpresa().getIdempresa());
            dto.setEmpresaNombre(u.getEmpresa().getRazonsocial());
        }

        if (u.getRol() != null) {
            dto.setRolId(u.getRol().getId());
        }

        return dto;
    }
}
