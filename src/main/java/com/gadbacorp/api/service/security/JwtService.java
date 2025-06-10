package com.gadbacorp.api.service.security;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.security.Key;

// Update the import path to the correct location of the User class
import com.gadbacorp.api.entity.security.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service

public class JwtService {

    @Value("${security.jwt.expirations-minutes}")
    private long EXPIRATION_MINUTES; //recibe el tiempo de expiracion en minutos de el toquen dirigido desde el aplication.properties
    @Value("${security.jwt.secrete-key}")
    private String SECRETE_KEY; //una clave secreta para generar el token encriptado, el algoritmo de encriptacion necesita una clave para empezar a encriptar

    public String generateToken(User user, Map<String, Object> extraClains) {
        Date issuedAt = new Date(System.currentTimeMillis()); //esto es el tiempo en elque se genera el token
        Date expiration = new Date(issuedAt.getTime() + (EXPIRATION_MINUTES*60*1000));//esto es en el tiempo en el que se expira el token ,
        //este es el contenido del token 
        return Jwts.builder()
        .setClaims(extraClains)
        .setSubject(user.getUsername())
        .setIssuedAt(issuedAt)
        .setExpiration(expiration)
        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
        .signWith(generateKey(), SignatureAlgorithm.HS256)
        .compact();

    }
    //se genera la clave secreta que se va a utilizar en el algoritmo de encriptacion
    private Key generateKey(){

        byte [] secreteByte = Decoders.BASE64.decode(SECRETE_KEY);
        return Keys.hmacShaKeyFor(secreteByte);

    }
    public String extracUsername(String jwt) {
        return extractAllClains(jwt).getSubject();
    }
    private Claims extractAllClains(String jwt) {
        return Jwts.parserBuilder().setSigningKey(generateKey()).build()
        .parseClaimsJws(jwt).getBody();
    }



}
