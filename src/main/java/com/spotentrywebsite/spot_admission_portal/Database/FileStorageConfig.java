package com.spotentrywebsite.spot_admission_portal.Database;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Exposes your backend disk upload directory to the admin URL pipeline
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:uploads/");
    }
}