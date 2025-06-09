package com.gadbacorp.api.gadbacorp.service.security;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import com.gadbacorp.api.gadbacorp.entity.dto.security.Authentication2FAResponse;
import com.gadbacorp.api.gadbacorp.entity.dto.security.AuthenticationRequest;
import com.gadbacorp.api.gadbacorp.entity.dto.security.AuthenticationResponse;
import com.gadbacorp.api.gadbacorp.entity.security.PasswordResetToken;
import com.gadbacorp.api.gadbacorp.entity.security.Permiso;
import com.gadbacorp.api.gadbacorp.entity.security.Rol;
import com.gadbacorp.api.gadbacorp.entity.security.User;
import com.gadbacorp.api.gadbacorp.repository.security.PasswordResetTokenRepository;
import com.gadbacorp.api.gadbacorp.repository.security.RolRepository;
import com.gadbacorp.api.gadbacorp.repository.security.UserRepository;


import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthenticationService {
    @Autowired
    private TwoFactorCodeService codeService;  // Generador de códigos aleatorios

    @Autowired
    private EmailService emailService;  // Servicio para enviar el código

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;
        @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private RolRepository rolRepository;

    // Método para loguearse
public Object login(AuthenticationRequest authRequest) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
    );

    User user = userRepository.findByUsername(authRequest.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException("No encontrado"));

    // Si tiene 2FA activado
    if (Boolean.TRUE.equals(user.is2faEnabled())) {
        // 1. Generar código aleatorio
        String code = codeService.generateCode();

        // 2. Guardar código y expiración
        user.setCode2fa(code);
        user.setCode2faExpiration(codeService.generateExpirationTime());
        userRepository.save(user);

        // 3. Enviar código por correo (puede ser user.getUsername() si es el correo)
        emailService.sendCodeEmail(user.getUsername(), code);

        // 4. Responder que se requiere verificación 2FA
        return new Authentication2FAResponse(true, "Se ha enviado un código a tu correo", user.getUsername());
    }

    // Si no tiene 2FA, generar el token como siempre
    String jwt = jwtService.generateToken(user, generateExtraClains(user));
    return new AuthenticationResponse(jwt);
}
    // Los extra claims (datos extras del usuario logueado)
    private Map<String, Object> generateExtraClains(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        extraClaims.put("name", user.getName());
        extraClaims.put("role", user.getRol().getNombre());

        // Extrae solo los nombres de permisos
        List<String> permisos = user.getRol().getPermisos().stream()
            .map(Permiso::getNombre)
            .collect(Collectors.toList());

        extraClaims.put("permissions", permisos);

        return extraClaims;
    }


    // Método que guarda un usuario
public User registerUser(User usuario) {
    if (usuario.getRol() == null || usuario.getRol().getId() == null) {
        throw new RuntimeException("El rol del usuario es requerido");
    }

    Rol rol = rolRepository.findById(usuario.getRol().getId())
        .orElseThrow(() -> new RuntimeException("Rol no válido"));

    usuario.setRol(rol); // Asignar objeto completo desde BD

    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    usuario.set2faEnabled(true);
    usuario.setCreatedAt(LocalDateTime.now());

    String code = codeService.generateCode();
    usuario.setCode2fa(code);
    usuario.setCode2faExpiration(codeService.generateExpirationTime());

    User savedUser = userRepository.save(usuario);
    emailService.sendCodeEmail(usuario.getUsername(), code);

    return savedUser;
}


    // Método para obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Método para actualizar un usuario
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        // Actualizar campos generales
        existingUser.setName(user.getName());
        existingUser.setUsername(user.getUsername());
        existingUser.setRol(user.getRol()); // Actualizar el rol según sea necesario
    
        // Solo cifrar la contraseña si se ha proporcionado una nueva
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
    
        return userRepository.save(existingUser);
    }

    // Método para eliminar un usuario
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

        // Método para obtener el usename del usuario logueado
    public User obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // El nombre de usuario del usuario autenticado
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));
    }

public AuthenticationResponse verify2FA(String username, String code) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    if (!user.is2faEnabled()) {
        throw new RuntimeException("2FA no habilitado para este usuario");
    }

    if (user.getCode2fa() == null || user.getCode2faExpiration() == null) {
        throw new RuntimeException("Código 2FA no generado o expirado");
    }

    if (!user.getCode2fa().equals(code)) {
        throw new RuntimeException("Código 2FA incorrecto");
    }

    if (LocalDateTime.now().isAfter(user.getCode2faExpiration())) {
        throw new RuntimeException("Código 2FA expirado");
    }

    // Código válido, limpia los datos
    user.setCode2fa(null);
    user.setCode2faExpiration(null);
    userRepository.save(user);

    String jwt = jwtService.generateToken(user, generateExtraClains(user));
    return new AuthenticationResponse(jwt);
}

public Authentication2FAResponse registerAsUser(User usuario) {
    // Asignar rol por defecto
    Rol rol = rolRepository.findByNombre("CLIENTE")
        .orElseThrow(() -> new RuntimeException("Rol por defecto 'CLIENTE' no encontrado"));

    usuario.setRol(rol);

    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
    usuario.set2faEnabled(true);
    usuario.setCreatedAt(LocalDateTime.now());

    String code = codeService.generateCode();
    usuario.setCode2fa(code);
    usuario.setCode2faExpiration(codeService.generateExpirationTime());

    User savedUser = userRepository.save(usuario);
    emailService.sendCodeEmail(usuario.getUsername(), code);

    return new Authentication2FAResponse(true, "Se ha enviado un código a tu correo", savedUser.getUsername());
}


public void requestPasswordReset(String email) {
    User user = userRepository.findByUsername(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con ese correo"));

    String code = codeService.generateCode(); // usa el mismo generador de 6 dígitos
    user.setCode2fa(code);
    user.setCode2faExpiration(codeService.generateExpirationTime()); // 5 o 10 min por defecto
    userRepository.save(user);

    emailService.sendCodeEmail(email, "Tu código de recuperación es: " + code);
}
public boolean validateResetCode(String email, String code) {
    User user = userRepository.findByUsername(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    if (user.getCode2fa() == null || user.getCode2faExpiration() == null)
        throw new RuntimeException("Código no generado o expirado");
    if (!user.getCode2fa().equals(code))
        throw new RuntimeException("Código incorrecto");
    if (LocalDateTime.now().isAfter(user.getCode2faExpiration()))
        throw new RuntimeException("Código expirado");

    // ✅ Limpiar el código y desactivar temporalmente la autenticación 2FA
    user.setCode2fa(null);
    user.setCode2faExpiration(null);
    //user.set2faEnabled(false); // o dejar en true si tu diseño lo requiere
    userRepository.save(user);

    return true;
}

public void updatePasswordWithCode(String email, String newPassword) {
    User user = userRepository.findByUsername(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

    // Ya debería haber sido validado antes
    user.setPassword(passwordEncoder.encode(newPassword));

    // ✅ Reactivar 2FA si lo desactivaste antes
    user.set2faEnabled(true);

    // Limpieza por si quedó algún residuo
    user.setCode2fa(null);
    user.setCode2faExpiration(null);

    userRepository.save(user);
}


}
