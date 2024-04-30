package com.saidqosimov.instagramvideodownloader.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Data
public class BotConfig {
    @Value("${bot.token}")
    String token;
    @Value("${bot.username}")
    String username;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
