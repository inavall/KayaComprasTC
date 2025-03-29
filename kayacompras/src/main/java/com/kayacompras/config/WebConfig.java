package com.kayacompras.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }
}
