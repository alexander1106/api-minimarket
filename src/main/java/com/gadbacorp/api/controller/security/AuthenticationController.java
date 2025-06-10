package com.gadbacorp.api.controller.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gadbacorp.api.entity.dto.security.AuthenticationRequest;
import com.gadbacorp.api.entity.dto.security.AuthenticationResponse;
import com.gadbacorp.api.entity.security.User;
import com.gadbacorp.api.service.security.AuthenticationService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/minimarket/auth")
@CrossOrigin("/**")
public class AuthenticationController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationService authenticationService;


    // @PreAuthorize("permitAll")
    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationRequest authRequest) {
        Object response = authenticationService.login(authRequest);
        return ResponseEntity.ok(response);
    }


    // @PreAuthorize("permitAll")
    @GetMapping("/public-acces")
    public String publicAccesEndpoint() {
        return "este endpoint es publico";
    }

    // @PreAuthorize("permitAll")
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody @Valid User usuario) {
        User newUser = authenticationService.registerUser(usuario);
        return ResponseEntity.ok(newUser);
    }
    // Público: crea usuario con rol USUARIO y 2FA activado
    // @PreAuthorize("permitAll")
    @PostMapping("/public-register")
    public ResponseEntity<?> publicRegister(@RequestBody @Valid User usuario) {
        Object newUser = authenticationService.registerAsUser(usuario);
        return ResponseEntity.ok(newUser);
    }

    // @PreAuthorize("hasAuthority('ACCEDER')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = authenticationService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // @PreAuthorize("permitAll")
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @Valid User user) {
        try {
            User updatedUser = authenticationService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            // Manejo de errores más específico
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    // @PreAuthorize("permitAll")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        authenticationService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-2fa")
    // @PreAuthorize("permitAll")
    public ResponseEntity<?> verify2fa(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String code = data.get("code");

        try {
            AuthenticationResponse jwtDto = authenticationService.verify2FA(username, code);
            return ResponseEntity.ok(jwtDto);
       } catch (Exception e) {
            // Log de detalles para debug
            log.error("Error al verificar 2FA: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Código 2FA inválido");
        }

    }


// @PreAuthorize("permitAll")
@PostMapping("/request-password-reset")
public ResponseEntity<?> requestReset(@RequestBody Map<String, String> data) {
    try {
        authenticationService.requestPasswordReset(data.get("email"));
        return ResponseEntity.ok("Se ha enviado un enlace para restablecer tu contraseña");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al procesar la solicitud");
    }
}



@PostMapping("/forgot-password")
public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
    authenticationService.requestPasswordReset(body.get("email"));
    return ResponseEntity.ok(Map.of("message", "Código enviado a tu correo"));
}

@PostMapping("/validate-code")
public ResponseEntity<Map<String, Object>> validateResetCode(@RequestBody Map<String, String> body) {
    Map<String, Object> response = new HashMap<>();
    try {
        authenticationService.validateResetCode(body.get("email"), body.get("code")); // ya no retorna booleano
        response.put("valid", true);
        response.put("message", "Código validado y autenticación reiniciada");
        return ResponseEntity.ok(response);
    } catch (RuntimeException ex) {
        response.put("valid", false);
        response.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}

@PostMapping("/update-password")
public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody Map<String, String> body) {
    Map<String, Object> response = new HashMap<>();
    try {
        authenticationService.updatePasswordWithCode(
            body.get("email"),
            body.get("newPassword") // ✅ solo email y password
        );
        response.put("success", true);
        response.put("message", "Contraseña actualizada correctamente");
        return ResponseEntity.ok(response);
    } catch (RuntimeException ex) {
        response.put("success", false);
        response.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}


}
