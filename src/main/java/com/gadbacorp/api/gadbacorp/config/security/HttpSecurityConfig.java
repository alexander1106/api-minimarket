/* package com.unsm.security.demo.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.unsm.security.demo.config.security.filter.JwtAuthenticationFilter;

@Component
@EnableWebSecurity                     // Habilita la configuración de seguridad web
@EnableMethodSecurity                  // Habilita @PreAuthorize y anotaciones relacionadas
public class HttpSecurityConfig {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .cors()  // << Habilita CORS desde Spring Security
        .and()
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // PERMITIR OPTIONS para preflight
            .requestMatchers(HttpMethod.POST, "/auth/authenticate").permitAll()
            .requestMatchers(HttpMethod.GET, "/auth/authenticate").permitAll()
            .requestMatchers(HttpMethod.POST, "/auth/public-acces").permitAll()
            .requestMatchers("/auth/forgot-password").permitAll()
            .requestMatchers( "/auth/validate-code").permitAll()
            .requestMatchers( "/auth/update-password").permitAll()
            .requestMatchers("/auth/validate-token").permitAll()
            .requestMatchers("/auth/public-acces").permitAll()
            .requestMatchers("/auth/public-register").permitAll()
            .requestMatchers("/auth/request-password-reset").permitAll()
            .requestMatchers("/auth/validate-reset-token").permitAll()
            .requestMatchers("/auth/reset-password").permitAll()
            .requestMatchers("/auth/verify-2fa").permitAll()
            .requestMatchers("/auth/register").hasAuthority("ACCEDER")
            .requestMatchers("/auth/users").hasAuthority("ACCEDER")
            .requestMatchers("/api/roles").hasAuthority("ACCEDER")
            .requestMatchers("/auth/**").hasAuthority("ACCEDER")

            // Permitir acceso a todos los roles
            //.requestMatchers("/auth/roles").hasRole("ADMINISTRADOR")

            .anyRequest().denyAll()
        )
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
}

}

 */
package com.gadbacorp.api.gadbacorp.config.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.gadbacorp.api.gadbacorp.config.security.filter.JwtAuthenticationFilter;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class HttpSecurityConfig {
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Endpoints públicos
                .requestMatchers("/oauth2/google").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/minimarket/auth/authenticate").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/minimarket/auth/authenticate").permitAll()
                .requestMatchers("/api/minimarket/auth/public-acces").permitAll()
                .requestMatchers("/api/minimarket/auth/public-register").permitAll()
                .requestMatchers("/api/minimarket/auth/forgot-password").permitAll()
                .requestMatchers("/api/minimarket/auth/verify-2fa").permitAll()
                .requestMatchers("/api/minimarket/auth/request-password-reset").permitAll()
                .requestMatchers("/api/minimarket/auth/update-password").permitAll()
                .requestMatchers("/api/minimarket/auth/validate-code").permitAll()
                .requestMatchers("/api/minimarket/auth/validate-reset-token").permitAll()
                .requestMatchers("/api/minimarket/auth/reset-password").permitAll()
                .requestMatchers("/api/minimarket/auth/validate-token").permitAll()
                // Endpoints protegidos
                
                // .requestMatchers("/api/minimarket/auth/**").hasAuthority("ACCESS_USUARIOS")

                // .requestMatchers("/api/minimarket/auth/**").hasAuthority("ACCESS_USUARIOS")
                .requestMatchers("/api/minimarket/auth/register").hasAuthority("ACCESS_USUARIOS")
                .requestMatchers("/api/minimarket/auth/users/**").hasAuthority("ACCESS_USUARIOS")
                .requestMatchers("/api/minimarket/roles/**").hasAuthority("ACCESS_USUARIOS")
                .requestMatchers("/api/minimarket/rol/**").hasAuthority("ACCESS_USUARIOS")

                .requestMatchers("/api/minimarket/empresas/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/sucursales/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/compras/**").hasAuthority("ACCESS_COMPRAS")
                .requestMatchers("/api/minimarket/delivery/**").hasAuthority("ACCESS_VENTAS")
                .requestMatchers("/api/minimarket/empleados/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/ajusteinventario/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/almacenes/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/almacenproducto/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/categorias/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/categoria/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/inventario/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/productos/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/promociones/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/tipoproducto/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/traslados/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/unidad-medida/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/clientes/**").hasAuthority("ACCESS_VENTAS")
                .requestMatchers("/api/minimarket/cliente/**").hasAuthority("ACCESS_VENTAS")
                .requestMatchers("/api/minimarket/detalles-venta/**").hasAuthority("ACCESS_VENTAS")
                .requestMatchers("/api/minimarket/detalles-ventas/**").hasAuthority("ACCESS_VENTAS")
                .requestMatchers("/api/minimarket/metodo-pago/**").hasAuthority("ACCESS_VENTAS")
                .requestMatchers("/api/minimarket/metodos-pago/**").hasAuthority("ACCESS_VENTAS")
                .requestMatchers("/api/minimarket/venta/**").hasAuthority("ACCESS_VENTAS")
                .requestMatchers("/api/minimarket/ventas/**").hasAuthority("ACCESS_VENTAS")
                .requestMatchers("/api/minimarket/modulos/**").hasAuthority("ACCESS_MANTENIMIENTO")
                .requestMatchers("/api/minimarket/modulo/**").hasAuthority("ACCESS_MANTENIMIENTO")
                


                .anyRequest().denyAll()
                )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
        
            "http://localhost:4200", 
            //"https://cpu-unsm-app.onrender.com",
            "http://localhost:8080" // para desarrollo local
            //"https://cpu-unsm-edu-pe.onrender.com" // ✅ frontend real en producción
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
