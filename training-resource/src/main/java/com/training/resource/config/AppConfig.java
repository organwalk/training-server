package com.training.resource.config;

import lombok.Getter;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AppConfig {
    @Value("${upload.normal-path}")
    private String resourceNormalPath;
    @Bean
    public Tika tika(){
        return new Tika();
    }
}
