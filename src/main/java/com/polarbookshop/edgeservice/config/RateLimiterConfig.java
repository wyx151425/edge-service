package com.polarbookshop.edgeservice.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Principal;

/**
 * @Author: WangZhenqi
 * @Description: 定义策略，为每个请求解析所使用的桶
 * @Date: Created in 2025-11-01 21:20
 * @Modified By:
 */
@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver keyResolver() {
//        // 使用固定键对请求进行限流
//        return exchange -> Mono.just("anonymous");
        // 从当前请求 (exchange) 中获取当前的认证用户 (principal)
        return exchange -> exchange.getPrincipal()
                // 从 principal 中抽取用户名
                .map(Principal::getName)
                // 如果请求未经认证，使用 “anonymous” 作为限流的默认键
                .defaultIfEmpty("anonymous");
    }
}

