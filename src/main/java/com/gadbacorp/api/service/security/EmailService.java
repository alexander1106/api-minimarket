package com.gadbacorp.api.service.security;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;  // <-- se inyecta automáticamente

    public void sendCodeEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);  // email del destinatario
        message.setSubject("Código de verificación 2FA");
        message.setText("Tu código de verificación es: " + code + "\n\nEste código es válido por 2 minutos.");
        mailSender.send(message);
    }
}
