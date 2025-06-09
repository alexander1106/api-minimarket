package com.gadbacorp.api.gadbacorp.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.gadbacorp.api.gadbacorp.repository.security.UserRepository;


@Component


public class SecurityBeansInjector {
    @Autowired
    private UserRepository userRepository;


//implementa el metodo que dice de que formas se haran la autenticaion de los usuarios puede ser con(/*
//cuenta de gogle, Oauth2)

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); //provaider manager

    }
//implementacion del metodo para probar la authenticacion 
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());

        return provider;

    }
//implementacion del metodo para encriptar las contraseñas
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();

    }


    //implementacion del metodo para obtener el usuario por su nombre de usuario(username)

    @Bean
    public UserDetailsService userDetailsService() {
        return username ->{
            return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));//usuario no encontrado en la base de datos
        };
    }


  




}
