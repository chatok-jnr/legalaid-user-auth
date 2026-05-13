package com.legalaid.userauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public HttpClient cloudinaryHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }
}

