package com.gadbacorp.api.service.security;

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

import com.gadbacorp.api.entity.dto.security.Authentication2FAResponse;
import com.gadbacorp.api.entity.dto.security.AuthenticationRequest;
import com.gadbacorp.api.entity.dto.security.AuthenticationResponse;
import com.gadbacorp.api.entity.security.PasswordResetToken;
import com.gadbacorp.api.entity.security.Permiso;
import com.gadbacorp.api.entity.security.Rol;
import com.gadbacorp.api.entity.security.User;
import com.gadbacorp.api.repository.security.PasswordResetTokenRepository;
import com.gadbacorp.api.repository.security.RolRepository;
import com.gadbacorp.api.repository.security.UserRepository;


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

    // 2FA
    if (Boolean.TRUE.equals(user.is2faEnabled())) {
        String code = codeService.generateCode();
        user.setCode2fa(code);
        user.setCode2faExpiration(codeService.generateExpirationTime());
        userRepository.save(user);
        emailService.sendCodeEmail(user.getUsername(), code);
        System.out.println("ROL antes de guardar: " + user.getRol());

        return new Authentication2FAResponse(true, "Se ha enviado un código a tu correo", user.getUsername());
    }

    // 🔁 REUTILIZAR TOKEN SI NO HA EXPIRADO
    if (user.getTokenJwt() != null && user.getTokenExpiracion() != null) {
        if (LocalDateTime.now().isBefore(user.getTokenExpiracion())) {
            return new AuthenticationResponse(user.getTokenJwt()); // Reusar
        }
    }

    // 🔄 GENERAR NUEVO TOKEN
    String jwt = jwtService.generateToken(user, generateExtraClains(user));
    user.setTokenJwt(jwt);
    user.setTokenExpiracion(LocalDateTime.now().plusHours(2)); // o el tiempo que uses
    user.setTokenSignature(generateUserSignature(user));
    userRepository.save(user);

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
    existingUser.setRol(user.getRol()); // Actualizar el rol

    // Solo cifrar la contraseña si se ha proporcionado una nueva
    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    // 🧠 Guardar primero los cambios
    userRepository.save(existingUser);

    // 🔄 Recargar el usuario con JOIN FETCH para tener el nuevo rol y permisos
       // 🧹 Borrar token y expiración
    existingUser.setTokenJwt(null);
    existingUser.setTokenExpiracion(null);
    existingUser.setTokenSignature(null);

    // Guardar los cambios y retornar el usuario actualizado
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
    // Limpiar código
    user.setCode2fa(null);
    user.setCode2faExpiration(null);

    // Reutilizar token si está aún vigente
    if (user.getTokenJwt() != null && user.getTokenExpiracion() != null) {
        if (LocalDateTime.now().isBefore(user.getTokenExpiracion())) {
            userRepository.save(user); // guardar la limpieza
            return new AuthenticationResponse(user.getTokenJwt());
        }
    }

    // Generar nuevo
    String jwt = jwtService.generateToken(user, generateExtraClains(user));
    user.setTokenJwt(jwt);
    user.setTokenExpiracion(LocalDateTime.now().plusHours(2));
    userRepository.save(user);

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
        // ⚠️ Solo si cambiaste el estado de 2FA, regenera token:
    String jwt = jwtService.generateToken(user, generateExtraClains(user));
    user.setTokenJwt(jwt);
    user.setTokenExpiracion(LocalDateTime.now().plusHours(2));
    user.setTokenSignature(generateUserSignature(user));

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
        String jwt = jwtService.generateToken(user, generateExtraClains(user));
    user.setTokenJwt(jwt);
    user.setTokenExpiracion(LocalDateTime.now().plusHours(2));
    user.setTokenSignature(generateUserSignature(user));


    userRepository.save(user);
}

private String generateUserSignature(User user) {
    return user.getUsername() + ":" + user.getRol().getNombre();
}

}
 
