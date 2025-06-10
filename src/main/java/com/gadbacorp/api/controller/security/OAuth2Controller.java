package com.gadbacorp.api.controller.security;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gadbacorp.api.config.GoogleTokenVerifier;
import com.gadbacorp.api.entity.dto.security.Authentication2FAResponse;
import com.gadbacorp.api.entity.dto.security.AuthenticationResponse;
import com.gadbacorp.api.entity.security.Permiso;
import com.gadbacorp.api.entity.security.Rol;
import com.gadbacorp.api.entity.security.User;
import com.gadbacorp.api.repository.security.RolRepository;
import com.gadbacorp.api.repository.security.UserRepository;
import com.gadbacorp.api.service.security.EmailService;
import com.gadbacorp.api.service.security.JwtService;
import com.gadbacorp.api.service.security.TwoFactorCodeService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;



@RestController
@RequestMapping("/oauth2")
@CrossOrigin("/**")
public class OAuth2Controller {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TwoFactorCodeService codeService;  // Generador de códigos aleatorios

    @Autowired
    private EmailService emailService;  // Servicio para enviar el código
    @Autowired
    private RolRepository rolRepository;
@PreAuthorize("permitAll")
@PostMapping("/google")
public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> body) {
    String googleToken = body.get("credential");

    GoogleIdToken.Payload payload = GoogleTokenVerifier.verifyToken(googleToken);
    if (payload == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
    }

    String email = payload.getEmail();
    String name = (String) payload.get("name");

    // Verifica si el usuario existe
    User user = userRepository.findByUsername(email).orElse(null);

    if (user == null) {
        // Nuevo usuario, crea con 2FA activado
        User nuevo = new User();
        nuevo.setUsername(email);
        nuevo.setName(name);
        nuevo.setCreatedAt(LocalDateTime.now());
        nuevo.set2faEnabled(true);
        nuevo.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // dummy password

        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
            .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));
        nuevo.setRol(rolCliente);

        // Genera código 2FA
        String code = codeService.generateCode();
        nuevo.setCode2fa(code);
        nuevo.setCode2faExpiration(codeService.generateExpirationTime());

        user = userRepository.save(nuevo);

        // Envía correo
        emailService.sendCodeEmail(user.getUsername(), code);

        return ResponseEntity.ok(new Authentication2FAResponse(
            true,
            "Te has registrado con Google. Se ha enviado un código de verificación a tu correo.",
            user.getUsername()
        ));
    }

    // Si ya existe y tiene 2FA activo, reenviar código
    if (user.is2faEnabled()) {
        String code = codeService.generateCode();
        user.setCode2fa(code);
        user.setCode2faExpiration(codeService.generateExpirationTime());
        userRepository.save(user);
        emailService.sendCodeEmail(user.getUsername(), code);

        return ResponseEntity.ok(new Authentication2FAResponse(
            true,
            "Se ha enviado un nuevo código de verificación a tu correo.",
            user.getUsername()
        ));
    }

    // Si ya existe y no requiere 2FA, autentica directamente
    Map<String, Object> extraClaims = new HashMap<>();
    extraClaims.put("id", user.getId());
    extraClaims.put("name", user.getName());
    extraClaims.put("role", user.getRol().getNombre());
    List<String> permisos = user.getRol().getPermisos().stream().map(Permiso::getNombre).toList();
    extraClaims.put("permissions", permisos);

    String jwt = jwtService.generateToken(user, extraClaims);
    return ResponseEntity.ok(new AuthenticationResponse(jwt));
}

}
