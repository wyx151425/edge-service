package com.polarbookshop.edgeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
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
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return http
                .authorizeExchange(
                        // 所有的请求都需要认证
                        exchange -> exchange.anyExchange().authenticated())
//                // 通过登录表单启用用户认证
//                .formLogin(Customizer.withDefaults())
                // 使用 OAuth2 / OpenID Connect 启用用户认证
                .oauth2Login(Customizer.withDefaults())
                // 定义一个自定义的处理器，用于退出操作成功完成的场景
                .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)))
                .build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        // 从 OIDC 提供者退出之后，将会重定向至应用的基础 URL，该 URL 是由 Spring 动态计算得到的 (本地的话，将会是 http://localhost:9000/)
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }
}
