package com.gadbacorp.api.controller.seguridad;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.config.JwtUtil;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.seguridad.LoginRequest;
import com.gadbacorp.api.entity.seguridad.ResetPasswordRequest;
import com.gadbacorp.api.excepciones.UsuarioDeshabilitadoException;
import com.gadbacorp.api.service.jpa.Empleados.UsuarioService;
import com.gadbacorp.api.service.jpa.seguridad.AuthService;
import com.gadbacorp.api.service.jpa.seguridad.UserDetailsServiceImpl;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/minimarket")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserDetailsService userDetailsService; // Asegúrate de registrar una implementación de UserDetailsService que devuelva `Usuarios`

    @Autowired
    private UserDetailsServiceImpl userxDetailsServiceImpl; 
    @Autowired
    private JwtUtil jwtUtils; 
    @Autowired
    private UsuarioService serviceUsuario;

@PostMapping("/login")
public ResponseEntity<?> generarToken(@RequestBody LoginRequest jwtRequest) throws Exception {
    // Autenticar
    autenticar(jwtRequest.getUsername(), jwtRequest.getPassword());

    // Cargar detalles del usuario
    UserDetails userDetails = userxDetailsServiceImpl.loadUserByUsername(jwtRequest.getUsername());

    // Generar token
    String token = jwtUtils.generateTokenUsua(userDetails);

    // Obtener usuario
    Usuarios usuario = serviceUsuario.obtenerUsuario(jwtRequest.getUsername());

    // Guardar token si quieres
    usuario.setToken(token);
    serviceUsuario.guardar(usuario);

    // Obtener el rol
    String rol = usuario.getRol().getNombre();

    // Retornar respuesta con token, rol y username
return ResponseEntity.ok(new AuthResponse(token, rol, usuario));
}


    private void autenticar(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException exception) {
            throw new UsuarioDeshabilitadoException("El usuario está deshabilitado.");
        } catch (BadCredentialsException e) {
            throw new Exception("Credenciales inválidas " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        return authService.resetPassword(request);
    }


   @GetMapping("/usuario-actual")
    public ResponseEntity<?> obtenerUsuarioActual(Principal principal){
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        Usuarios usuario = (Usuarios) this.userxDetailsServiceImpl.loadUserByUsername(principal.getName());
        return ResponseEntity.ok(usuario);
    }

}
