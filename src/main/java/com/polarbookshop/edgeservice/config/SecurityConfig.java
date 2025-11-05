package com.polarbookshop.edgeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * @Author: WangZhenqi
 * @Description: 安全性相关的配置类，所有的端点都需要通过登录表单进行认证
 * @Date: Created in 2025-11-05 18:51
 * @Modified By:
 */
@EnableWebFluxSecurity
public class SecurityConfig {

    // SecurityWebFilter bean 用来定义和配置应用的安全策略
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(
                        // 所有的请求都需要认证
                        exchange -> exchange.anyExchange().authenticated())
//                // 通过登录表单启用用户认证
//                .formLogin(Customizer.withDefaults())
                // 使用 OAuth2 / OpenID Connect 启用用户认证
                .oauth2Login(Customizer.withDefaults())
                .build();
    }
}
