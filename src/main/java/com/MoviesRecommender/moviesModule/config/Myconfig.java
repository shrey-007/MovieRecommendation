package com.MoviesRecommender.moviesModule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Myconfig {
    @Bean
    // This object is used to call external API, in our case it is flask application
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
