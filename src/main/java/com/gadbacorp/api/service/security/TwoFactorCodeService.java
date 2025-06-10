package com.gadbacorp.api.service.security;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.security.SecureRandom;

@Service
public class TwoFactorCodeService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    public String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }

    public LocalDateTime generateExpirationTime() {
        return LocalDateTime.now().plusMinutes(2);
    }
}
