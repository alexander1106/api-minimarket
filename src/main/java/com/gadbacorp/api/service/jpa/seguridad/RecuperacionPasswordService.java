package com.gadbacorp.api.service.jpa.seguridad;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.repository.empleados.UsuarioRepository;
import com.gadbacorp.api.repository.seguridad.PasswordResetTokenRepository;

/** Genera el token de recuperacion (1 hora de vigencia) y envia el enlace por correo. */
@Service
public class RecuperacionPasswordService {

    private static final Logger log = LoggerFactory.getLogger(RecuperacionPasswordService.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final ObjectProvider<JavaMailSender> mailSender;
    private final String frontendUrl;
    private final String remitente;
    private final boolean correoConfigurado;

    public RecuperacionPasswordService(UsuarioRepository usuarioRepository,
                                       PasswordResetTokenRepository tokenRepository,
                                       ObjectProvider<JavaMailSender> mailSender,
                                       @Value("${app.frontend-url:http://localhost:4200}") String frontendUrl,
                                       @Value("${spring.mail.username:no-reply@minimarket.local}") String remitente,
                                       @Value("${spring.mail.host:}") String mailHost) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.frontendUrl = frontendUrl;
        this.remitente = remitente;
        this.correoConfigurado = mailHost != null && !mailHost.isBlank();
    }

    /**
     * Siempre termina sin error aunque el correo no exista, para no revelar que cuentas estan registradas.
     */
    @Transactional
    public void enviarLink(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio.");
        }
        Optional<Usuarios> usuario = usuarioRepository.findByEmail(email.trim());
        if (usuario.isEmpty()) {
            log.info("Recuperacion solicitada para un correo no registrado");
            return;
        }

        // un solo token vigente por usuario
        tokenRepository.findByUsuario(usuario.get()).ifPresent(tokenRepository::delete);
        tokenRepository.flush();

        PasswordResetToken token = new PasswordResetToken(UUID.randomUUID().toString());
        token.setUsuario(usuario.get());
        token.setFechaExpiracion(LocalDateTime.now().plusHours(1));
        tokenRepository.save(token);

        String enlace = frontendUrl + "/reset-password?token=" + token.getToken();
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null || !correoConfigurado) {
            log.warn("No hay servidor de correo configurado (spring.mail.host). Enlace de recuperacion no enviado.");
            return;
        }
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(usuario.get().getEmail());
        mensaje.setSubject("Recuperación de contraseña - Minimarket");
        mensaje.setText("Para restablecer tu contraseña ingresa al siguiente enlace (vence en 1 hora):\n\n"
                + enlace + "\n\nSi no solicitaste este cambio, ignora este mensaje.");
        try {
            sender.send(mensaje);
        } catch (MailException e) {
            // el token ya quedo guardado; un fallo del servidor de correo no debe revelar nada ni romper la peticion
            log.error("No se pudo enviar el correo de recuperacion", e);
        }
    }
}
