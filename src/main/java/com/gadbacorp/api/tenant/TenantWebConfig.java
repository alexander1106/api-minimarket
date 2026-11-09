package com.gadbacorp.api.tenant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Aislamiento por empresa en tres puntos, sin tocar cada controlador:
 * 1. la URL ({@code /ventas/9}) no puede apuntar a un registro de otra empresa;
 * 2. el cuerpo recibido no puede apuntar a sucursales o registros de otra empresa;
 * 3. lo que se responde se filtra: las listas solo traen lo propio y un registro ajeno es un 404.
 */
@Configuration
public class TenantWebConfig implements WebMvcConfigurer {

    private static final String PREFIJO = "/api/minimarket/";

    private final TenantGuard guard;

    public TenantWebConfig(TenantGuard guard) {
        this.guard = guard;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String ruta = request.getRequestURI();
                int i = ruta.indexOf(PREFIJO);
                if (i >= 0) {
                    guard.validarRuta(ruta.substring(i + PREFIJO.length()));
                }
                return true;
            }
        }).addPathPatterns("/api/minimarket/**");
    }

    /** Valida los cuerpos recibidos. */
    @RestControllerAdvice
    public static class Entrada extends RequestBodyAdviceAdapter {
        private final TenantGuard guard;

        public Entrada(TenantGuard guard) {
            this.guard = guard;
        }

        @Override
        public boolean supports(MethodParameter parameter, java.lang.reflect.Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
            return true;
        }

        @Override
        public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter,
                                    java.lang.reflect.Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
            if (body instanceof Collection<?> elementos) {
                elementos.forEach(guard::validarCuerpo);
            } else {
                guard.validarCuerpo(body);
            }
            return body;
        }
    }

    /** Filtra las respuestas: cada empresa solo recibe lo suyo. */
    @RestControllerAdvice
    public static class Salida implements ResponseBodyAdvice<Object> {
        private final TenantRules reglas;

        public Salida(TenantRules reglas) {
            this.reglas = reglas;
        }

        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
            return true;
        }

        @Override
        public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                      Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                      ServerHttpRequest request, ServerHttpResponse response) {
            if (body == null || !TenantService.restringido()) {
                return body;
            }
            Object filtrado = filtrar(body);
            if (filtrado == AJENO) {
                response.setStatusCode(HttpStatus.NOT_FOUND);
                return Map.of("message", "Recurso no encontrado");
            }
            return filtrado;
        }

        private static final Object AJENO = new Object();

        /** @return el mismo valor con lo ajeno quitado, o {@link #AJENO} si el valor entero es de otra empresa */
        private Object filtrar(Object valor) {
            if (valor instanceof Optional<?> opcional) {
                return opcional.isPresent() && filtrar(opcional.get()) == AJENO ? AJENO : valor;
            }
            if (valor instanceof Collection<?> coleccion) {
                List<Object> visibles = new ArrayList<>();
                for (Object elemento : coleccion) {
                    Object f = filtrar(elemento);
                    if (f != AJENO) {
                        visibles.add(f);
                    }
                }
                return visibles.size() == coleccion.size() ? valor : visibles;
            }
            if (valor instanceof Map<?, ?> mapa) {
                Map<Object, Object> copia = new java.util.LinkedHashMap<>();
                for (Map.Entry<?, ?> e : mapa.entrySet()) {
                    Object f = filtrar(e.getValue());
                    if (f == AJENO) {
                        return AJENO;
                    }
                    copia.put(e.getKey(), f);
                }
                return copia;
            }
            if (reglas.protegido(valor) && !TenantService.puedeVer(reglas.empresaDe(valor))) {
                return AJENO;
            }
            return valor;
        }
    }
}
