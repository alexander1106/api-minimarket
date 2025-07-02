package com.gadbacorp.api.controller.seguridad;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.empleados.UsuariosDTO;
import com.gadbacorp.api.entity.seguridad.Rol;
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
    private JwtUtil jwtUtil;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    @GetMapping("/usuarios")
    public List<Usuarios> buscarTodos() {
        return usuariosService.buscarTodos();
    }
    @PostMapping("/usuarios")
    public ResponseEntity<?> guardar(@RequestBody UsuariosDTO dto) {

        Sucursales sucursales= sucursalesRepository.findById(dto.getIdSucursal()).orElse(null);
        if (sucursales == null) {
            return ResponseEntity.badRequest().body("sucursal no encontrado con ID: " + dto.getIdSucursal());
        }

        Rol rol = rolRepository.findById(dto.getRolId()).orElse(null);
        if (rol == null) {
            return ResponseEntity.badRequest().body("rol no encontrado con ID: " + dto.getRolId());
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
    
    @PutMapping("/usuarios") //La diferencia acá es que subiremos el id, no ponemos "/clientes/id" por temas de seguridad, acuérdate de la caja de la unidad 2, eso no era correcto
    public Usuarios modificar(@RequestBody Usuarios empleado) {        
        usuariosService.modificar(empleado);
        return empleado;
    }


    @GetMapping("/usuarios/{id}")
    public Optional<Usuarios> buscarId(@PathVariable("id") Integer id){
        return usuariosService.buscarId(id);
    }

    @DeleteMapping("/usuarios/{id}")
    public String eliminar(@PathVariable Integer id){
        usuariosService.eliminar(id);
        return "Usuario elimnado";
    }
}
