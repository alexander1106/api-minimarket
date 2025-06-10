package com.gadbacorp.api.config.security.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// Update the import below to the correct package where User is defined
import com.gadbacorp.api.entity.security.User;
import com.gadbacorp.api.repository.security.UserRepository;
import com.gadbacorp.api.service.security.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@Component
public class JwtAuthenticationFilter  extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        //1. obtener el header que contiene el jwt
        String authHeader = request.getHeader("Authorization"); //berear jwt

        if (authHeader ==null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
            
        }
        
        //2. sacar el jwt de ese header
        String jwt = authHeader.split(" ")[1];

        //3. sacar el subject/username desde el jwt
        String username = jwtService.extracUsername(jwt);

        //4. setear un objeto authentication dentro del securitycontex

        User user = userRepository.findByUsername(username).get();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            username, null , user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

        //5. ejecutar el resto de filtros
        filterChain.doFilter(request, response);
    }

}
