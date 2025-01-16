package com.innowell.core.core.config;

import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {

    private Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${api.base.url}")
    private String apiBaseUrl;

    @Value("${api.key.header}")
    private String apiKeyHeader;

    @Value("${api.key}")
    private String apiKey;


    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(apiBaseUrl)
            .defaultHeader(apiKeyHeader,apiKey)
            .defaultHeader("Content-Type", "application/json")
            .filter((request, next) -> {
                logger.info("Request URL: " + request.url());
                logger.info("Request Headers: " + request.headers().toString());
                return next.exchange(request);
            })
            .build();
    }
}
