package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class GatewayConfigurations {
    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schemaName;
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
