package com.gadbacorp.api.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${upload.dir}")
    private String uploadDir;  
    // debe valer, por ejemplo: /home/rapimarket/public_html/uploads

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ruta absoluta limpia
        String absolutePath = Paths.get(uploadDir)
                                   .toAbsolutePath()
                                   .normalize()
                                   .toString();

        // IMPORTANTE: prefijo "file:" + carpeta + "/" al final
        String resourceLocation = "file:" + absolutePath + "/";

        registry
          .addResourceHandler("/uploads/**")
          .addResourceLocations(resourceLocation);
    }
}
