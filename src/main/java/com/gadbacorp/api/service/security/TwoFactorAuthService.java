package com.gadbacorp.api.service.security;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorAuthService {

    public String generateSecret() {
        return Base32.random(); // guardar esto en BD (campo `secret2fa`)
    }

    public boolean verifyCode(String secret, String code) {
        Totp totp = new Totp(secret);
        return totp.verify(code); // true si el código es correcto
    }

}
