package com.gadbacorp.api.service.jpa.seguridad;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.config.JwtUtil;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.seguridad.LoginRequest;
import com.gadbacorp.api.entity.seguridad.ResetPasswordRequest;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.seguridad.PasswordResetTokenRepository;
@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // BCryptPasswordEncoder


 

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