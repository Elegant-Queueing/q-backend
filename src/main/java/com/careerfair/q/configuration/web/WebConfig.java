package com.careerfair.q.configuration.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.*;

import javax.jws.WebService;

@Configuration
@CrossOrigin
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("PUT", "POST", "GET", "OPTIONS", "DELETE")
                .allowedOrigins("*");
    }
}
