package com.training.resource.config;

import lombok.Getter;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置文件路径
 * by organwalk 2023-11-02
 */
@Configuration
@Getter
public class AppConfig {
    @Value("${upload.normal-path}")
    private String resourceNormalPath;
    @Value("${upload.lesson-path}")
    private String lessonPath;
    @Value("${upload.note-path}")
    private String notePath;
    @Bean
    public Tika tika(){
        return new Tika();
    }
}
