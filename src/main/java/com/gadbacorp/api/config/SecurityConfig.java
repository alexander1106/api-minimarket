package com.gadbacorp.api.config;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.gadbacorp.api.service.jpa.seguridad.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private com.gadbacorp.api.service.jpa.auditoria.AuditoriaService auditoriaService;

    @Value("${app.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Value("${app.oauth2.enabled:false}")
    private boolean oauth2Enabled;

    /** Autorizacion por rol de las operaciones de escritura. Desactivable con APP_ENFORCE_ROLES=false. */
    @Value("${app.security.enforce-roles:true}")
    private boolean enforceRoles;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            ObjectProvider<JwtDecoder> oauth2JwtDecoder) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Sin sesion valida (sin token, vencido o alterado) = 401, para que el frontend cierre la sesion.
            // Con sesion pero sin permiso sobre el modulo = 403 (lo responde AccessDeniedHandler por defecto).
            .exceptionHandling(e -> e.authenticationEntryPoint((request, response, ex) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Sesión inválida o vencida. Inicia sesión nuevamente.\"}");
            }))
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers(
                    "/api/minimarket",
                    "/api/minimarket/login",
                    "/api/minimarket/enviar-link-recuperacion",
                    "/api/minimarket/reset-password",
                    "/actuator/health/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll();
                // El registro de clientes API es publico; consultarlo o modificarlo no
                auth.requestMatchers(HttpMethod.POST, API + "/registros").permitAll();

                // La auditoria es solo del dueno de la plataforma, aunque se desactive la autorizacion por modulos
                auth.requestMatchers(API + "/auditoria/**").hasAuthority(SUPERADMIN);

                if (enforceRoles) {
                    reglasPorRol(auth);
                }
                auth.anyRequest().authenticated();
            })
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(new AuditoriaFilter(auditoriaService), org.springframework.security.web.access.intercept.AuthorizationFilter.class);

        // OAuth2 Resource Server opcional (tokens de un IdP externo: Keycloak, Auth0, Azure AD...)
        if (oauth2Enabled && oauth2JwtDecoder.getIfAvailable() != null) {
            DefaultBearerTokenResolver defaultResolver = new DefaultBearerTokenResolver();
            // Si el filtro propio ya autentico el token, el resource server no lo vuelve a procesar
            BearerTokenResolver resolver = request ->
                    SecurityContextHolder.getContext().getAuthentication() != null
                            ? null : defaultResolver.resolve(request);
            http.oauth2ResourceServer(o -> o.bearerTokenResolver(resolver).jwt(Customizer.withDefaults()));
        }

        return http.build();
    }

    private static final String API = "/api/minimarket";
    private static final HttpMethod[] ESCRITURA =
            {HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH, HttpMethod.DELETE};

    private static final String SUPERADMIN = "SUPERADMIN";

    @Autowired
    private PermisosService permisosService;

    /**
     * Los permisos salen de la tabla rol_modulo (la misma que arma el menu del frontend):
     * un rol puede MODIFICAR (POST/PUT/PATCH/DELETE) un modulo solo si esta asignado a el.
     * Consultar (GET) lo puede hacer cualquier usuario autenticado, salvo SUPERADMIN en los modulos operativos
     * (el dueno de la plataforma administra empresas, pero no ve los datos de cada negocio).
     * Excepciones fijas en codigo: SUPERADMIN siempre administra el modulo Administrable y puede crear usuarios
     * (necesario para dar de alta al usuario EMPRESA de cada empresa).
     */
    private void reglasPorRol(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        // Los metodos de pago se crean dentro de una sucursal: pertenecen a Ventas, no a Sucursales
        operativo(auth, "Ventas", API + "/sucursales/*/metodos-pago", API + "/sucursales/*/metodos-pago/**");

        administrable(auth, API + "/empresas/**", API + "/sucursales/**", API + "/roles/**",
                API + "/modulos/**", API + "/rolesmodulos/**", API + "/registros/**", API + "/planes/**");

        auth.requestMatchers(HttpMethod.GET, API + "/empresas/*/usuarios").hasAuthority(SUPERADMIN);
        // Usuarios (modulo Seguridad); SUPERADMIN solo para crear al usuario EMPRESA de cada empresa
        escritura(auth, new String[] {API + "/usuarios/**"}, exigeModulo("Seguridad", true));

        // Unidades, categorias y tipos de producto: catalogos globales de la plataforma.
        // Todos los consultan; solo SUPERADMIN los modifica
        escritura(auth, new String[] {API + "/unidad_medida/**", API + "/categorias/**", API + "/tipoproducto/**",
                API + "/metodos-pago-catalogo/**"}, (autenticacion, contexto) -> new AuthorizationDecision(
                estaAutenticado(autenticacion.get()) && autenticacion.get().getAuthorities().stream()
                        .anyMatch(g -> SUPERADMIN.equals(g.getAuthority()))));

        operativo(auth, "Inventario", API + "/kardex/**", API + "/lotes/**", API + "/inventario/**", API + "/inventarioproducto/**", API + "/ajustes/**",
                API + "/traslados/**");
        operativo(auth, "Almacenes", API + "/almacenes/**");
        operativo(auth, "Productos", API + "/productos/**", API + "/promociones/**", API + "/presentaciones/**");
        // Intercambio con el Host: exige el modulo Reportes tambien para consultar (el archivo contiene ventas)
        escritura(auth, new String[] {API + "/host/**"}, exigeModulo("Reportes", false));
        auth.requestMatchers(HttpMethod.GET, API + "/host/**").access(exigeModulo("Reportes", false));
        operativo(auth, "Compras", API + "/compras/**", API + "/detalles-compras/**", API + "/proveedores/**");
        operativo(auth, "Ventas", API + "/ventas/**", API + "/detalles-ventas/**", API + "/cotizaciones/**",
                API + "/detalles-cotizaciones/**", API + "/clientes/**", API + "/metodos-pago/**",
                API + "/devoluciones/**", API + "/detalles-devoluciones/**");
        operativo(auth, "Delivery", API + "/delivery/**", API + "/vehiculo/**", API + "/estadomovimiento/**");
        operativo(auth, "Caja", API + "/pagos/**", API + "/cajas/**", API + "/cajas-abiertas",
                API + "/aperturas-cajas/**", API + "/aperturas-caja/**", API + "/transacciones-cajas/**",
                API + "/transaccion-cajas/**", API + "/transaccion-caja/**", API + "/transferencias-entre-cajas/**",
                API + "/transeferencias-entre-cajas/**", API + "/arqueos-cajas/**", API + "/saldo-metodo/**");
    }

    /** Administrable: lectura abierta (el login lee la empresa de la sucursal); escribe SUPERADMIN o quien tenga el modulo. */
    private void administrable(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth,
                               String... rutas) {
        escritura(auth, rutas, exigeModulo("Administrable", true));
    }

    /** Modulo operativo: escribe quien lo tenga asignado en rol_modulo; SUPERADMIN no lo consulta. */
    private void operativo(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth,
                           String modulo, String... rutas) {
        escritura(auth, rutas, exigeModulo(modulo, false));
        auth.requestMatchers(HttpMethod.GET, rutas).access((autenticacion, contexto) -> {
            Authentication a = autenticacion.get();
            boolean permitido = estaAutenticado(a)
                    && a.getAuthorities().stream().noneMatch(g -> SUPERADMIN.equals(g.getAuthority()));
            return new AuthorizationDecision(permitido);
        });
    }

    private AuthorizationManager<RequestAuthorizationContext> exigeModulo(String modulo, boolean superadminSiempre) {
        return (autenticacion, contexto) -> {
            Authentication a = autenticacion.get();
            if (!estaAutenticado(a)) {
                return new AuthorizationDecision(false);
            }
            for (GrantedAuthority g : a.getAuthorities()) {
                String rol = g.getAuthority();
                if ((superadminSiempre && SUPERADMIN.equals(rol)) || permisosService.rolTieneModulo(rol, modulo)) {
                    return new AuthorizationDecision(true);
                }
            }
            return new AuthorizationDecision(false);
        };
    }

    private static boolean estaAutenticado(Authentication a) {
        return a != null && a.isAuthenticated() && !(a instanceof AnonymousAuthenticationToken);
    }

    private void escritura(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth,
                           String[] rutas, AuthorizationManager<RequestAuthorizationContext> regla) {
        for (HttpMethod metodo : ESCRITURA) {
            auth.requestMatchers(metodo, rutas).access(regla);
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList();
        cfg.setAllowedOriginPatterns(origins);
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }
}
