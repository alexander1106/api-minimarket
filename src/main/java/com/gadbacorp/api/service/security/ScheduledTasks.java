package com.gadbacorp.api.service.security;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gadbacorp.api.entity.security.User;
import com.gadbacorp.api.repository.security.UserRepository;

@Component
public class ScheduledTasks {

    @Autowired
    private UserRepository userRepository;

@Scheduled(fixedRate = 30000) // Cada minuto
public void eliminarUsuariosNoVerificados() {
    LocalDateTime ahora = LocalDateTime.now();
    LocalDateTime hace2Min = ahora.minusMinutes(2);
    LocalDateTime hace2Min30Seg = ahora.minusMinutes(2).minusSeconds(30);

    List<User> usuarios = userRepository.findAll();

    for (User user : usuarios) {
        boolean tiene2FApendiente = user.is2faEnabled()
                && user.getCode2fa() != null
                && user.getCode2faExpiration() != null;

        boolean dentroDelRango = user.getCreatedAt() != null
                && user.getCreatedAt().isBefore(hace2Min)
                && user.getCreatedAt().isAfter(hace2Min30Seg);

        if (tiene2FApendiente && dentroDelRango) {
            userRepository.delete(user);
        }
    }
}

}
