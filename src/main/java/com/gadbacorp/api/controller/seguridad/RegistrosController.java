package com.gadbacorp.api.controller.seguridad;

import java.util.Collections;
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
import com.gadbacorp.api.entity.seguridad.Registros;
import com.gadbacorp.api.service.seguridad.IRegistrosService;

@RestController
@RequestMapping("/api/minimarket")
@CrossOrigin(origins = "*") // o especifica el dominio si es necesario
public class RegistrosController {
    @Autowired
    private IRegistrosService serviceRegistros;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/registros")
    public List<Registros> buscarTodos() {
        return serviceRegistros.buscarTodos();
    }

   

    @PutMapping("/registros")
    public Registros modificar(@RequestBody Registros registro) {
        serviceRegistros.modificar(registro);
        return registro;
    }

    @GetMapping("/registros/{id}")
    public Optional<Registros> buscarId(@PathVariable("id") Integer id){
        return serviceRegistros.buscarId(id);
    }

    @DeleteMapping("/registros/{id}")
    public String eliminar(@PathVariable Integer id){
        serviceRegistros.eliminar(id);
        return "Registro eliminado";
    }
    
    @PostMapping("/registros")
    public ResponseEntity<?> guardar(@RequestBody Registros registro) {
        registro.setCliente_id(null);
        String claveOriginal = registro.getEmail() + registro.getNombres() + registro.getApellidos();
        String claveEncriptada = passwordEncoder.encode(claveOriginal);
        registro.setLlave_secreta(claveEncriptada);
        serviceRegistros.guardar(registro);
        String clienteId = registro.getCliente_id(); // Asegúrate que se actualice tras guardar
        String token = jwtUtil.generarToken(clienteId);
        registro.setAccess_token(token);
        serviceRegistros.guardar(registro); // Actualizar con el token
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }
}
