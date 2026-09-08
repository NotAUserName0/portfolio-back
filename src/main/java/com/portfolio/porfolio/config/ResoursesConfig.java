package com.portfolio.porfolio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @description Configures static resource handlers to serve uploaded images publicly.
 */
@Configuration
public class ResoursesConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:upload/}")
    private String uploadDir;

    /**
     * @description Maps the /upload/** URL pattern to the physical upload directory on disk.
     *              Uses canonical file URI to guarantee cross-platform compatibility (Windows/Linux/Docker).
     * @param registry The ResourceHandlerRegistry to add handlers to.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String locationUri = uploadPath.toUri().toString();
        if (!locationUri.endsWith("/")) {
            locationUri += "/";
        }

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(locationUri);
    }
}
