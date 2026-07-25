package com.example.septic.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private final SeoQueryRobotsInterceptor seoQueryRobotsInterceptor;

    public WebConfiguration(SeoQueryRobotsInterceptor seoQueryRobotsInterceptor) {
        this.seoQueryRobotsInterceptor = seoQueryRobotsInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(seoQueryRobotsInterceptor);
    }
}
