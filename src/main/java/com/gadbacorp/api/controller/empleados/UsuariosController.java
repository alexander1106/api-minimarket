package com.gadbacorp.api.controller.empleados;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.management.relation.Role;

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
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.repository.administrable.SucursalesRepository;
import com.gadbacorp.api.repository.seguridad.RolRepository;
import com.gadbacorp.api.service.empleados.IUsuariosService;
import com.gadbacorp.api.service.jpa.administrable.SucursalesService;

@RestController
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

       Rol rol = rolRepository.findById(dto.getRollId()).orElse(null);
        if (rol == null) {
            return ResponseEntity.badRequest().body("rol no encontrado con ID: " + dto.getRollId());
        }

        String claveOriginal =  dto.getNombre()
                                    + dto.getApellidos();
        Usuarios usuarios= new Usuarios();
        
        usuarios.setContrasenaHash(claveOriginal);
        usuarios.setApellidos(dto.getApellidos());
        usuarios.setNombre(dto.getNombre());
        usuarios.setSucursal(sucursales);
        usuarios.setCorreoElectronico(dto.getCorreoElectronico());
        usuarios.setRollId(dto.getRollId());
        usuarios.setFechaCreacion(dto.getFechaCreacion());

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
