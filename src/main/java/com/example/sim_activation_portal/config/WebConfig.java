package com.example.sim_activation_portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Interceptor disabled to allow all API calls (no session check)
        // registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/api/**");
    }
}