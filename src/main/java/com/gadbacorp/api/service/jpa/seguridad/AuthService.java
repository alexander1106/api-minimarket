package com.gadbacorp.api.service.jpa.seguridad;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.seguridad.PasswordResetTokenRepository;
@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // BCryptPasswordEncoder

    // LOGIN
    public ResponseEntity<?> login(LoginRequest request) {
        Optional<Usuarios> usuarioOpt = usuarioRepository.findByUsername(request.getUsername());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Usuarios usuario = usuarioOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        // Aquí podrías generar un JWT o crear sesión
        return ResponseEntity.ok("Login exitoso para: " + usuario.getUsername());
    }

    // ENVIAR TOKEN DE RECUPERACIÓN
    public ResponseEntity<?> sendResetToken(String email) {
        Optional<Usuarios> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Correo no registrado");
        }

        Usuarios usuario = usuarioOpt.get();

        String token = UUID.randomUUID().toString();
        LocalDateTime expiracion = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUsuario(usuario);
        resetToken.setFechaExpiracion(expiracion);

        tokenRepository.save(resetToken);

        // Simulación de envío de correo
        System.out.println("Token enviado al correo: " + token);

        return ResponseEntity.ok("Se envió el token de recuperación");
    }

    // RESTABLECER CONTRASEÑA
    public ResponseEntity<?> resetPassword(ResetPasswordRequest request) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(request.getToken());

        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido");
        }

        PasswordResetToken token = tokenOpt.get();

        if (token.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expirado");
        }

        Usuarios usuario = token.getUsuario();
        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);

        // Opcional: eliminar el token luego de usarlo
        tokenRepository.delete(token);

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }
}