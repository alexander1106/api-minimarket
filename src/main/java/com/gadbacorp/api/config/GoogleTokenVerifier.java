package com.gadbacorp.api.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;

public class GoogleTokenVerifier {
    private static final String CLIENT_ID = "208653476798-6tsh9o9efl15l3occ12fdjsahrafqob8.apps.googleusercontent.com";


public static GoogleIdToken.Payload verifyToken(String idTokenString) {
    try {
        System.out.println("Verificando token: " + idTokenString);
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance()
        ).setAudience(Collections.singletonList(CLIENT_ID)).build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            System.out.println("Token válido: " + idToken.getPayload());
            return idToken.getPayload();
        } else {
            System.out.println("Token inválido");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

}

