package com.ghifar.userlogin.userlogin.config;
//this class is only for set our CORS because our frontend will run at different server.
//this class not limited to config CORS only, this class can do more..

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS= 3600;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("HEAD","OPTIONS","GET","POST","PUT", "PATCH", "PATCH", "DELETE")
                .maxAge(MAX_AGE_SECS);
    }
}
