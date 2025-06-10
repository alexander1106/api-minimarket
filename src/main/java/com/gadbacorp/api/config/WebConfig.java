package com.gadbacorp.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ¡Esta línea es la clave!
        registry.addResourceHandler("/uploads/categorias/**")
                .addResourceLocations("file:uploads/categorias/");
    }
}