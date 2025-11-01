package com.polarbookshop.edgeservice.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * @Author: WangZhenqi
 * @Description: 定义策略，为每个请求解析所使用的同
 * @Date: Created in 2025-11-01 21:20
 * @Modified By:
 */
@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver keyResolver() {
        // 使用固定键对请求进行限流
        return exchange -> Mono.just("anonymous");
    }
}

