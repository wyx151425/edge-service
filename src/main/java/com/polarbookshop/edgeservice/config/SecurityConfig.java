package com.polarbookshop.edgeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

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
                // 所有的请求都需要认证
                .authorizeExchange(exchange -> exchange
                        // 允许对 SPA 静态资源进行未认证访问
                        .pathMatchers("/", "/*.css", "/*.js", "/favicon.ico").permitAll()
                        // 允许对目录中的图书进行未认证的读取访问
                        .pathMatchers(HttpMethod.GET, "/books/**").permitAll()
                        // 任何其他的请求都需要用户认证
                        .anyExchange().authenticated()
                )
                // 当因为用户没有认证而抛出异常时，将会返回 HTTP 401 响应
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
//                // 通过登录表单启用用户认证
//                .formLogin(Customizer.withDefaults())
                // 使用 OAuth2 / OpenID Connect 启用用户认证
                .oauth2Login(Customizer.withDefaults())
                // 定义一个自定义的处理器，用于退出操作成功完成的场景
                .logout(logout -> logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository)))
                // 使用基于 cookie 的策略来与 Angular 前端交换 CSRF
                .csrf(csrf -> csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
                .build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        var oidcLogoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        // 从 OIDC 提供者退出之后，将会重定向至应用的基础 URL，该 URL 是由 Spring 动态计算得到的 (本地的话，将会是 http://localhost:9000/)
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
        return oidcLogoutSuccessHandler;
    }

    // 仅用于订阅 CsrfToken 反应式流的过滤器，并确保它的值能够被正常提取
    @Bean
    WebFilter csrfWebFilter() {
        return (exchange, chain) -> {
            exchange.getResponse().beforeCommit(() -> Mono.defer(() -> {
                Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
                return csrfToken != null ? csrfToken.then() : Mono.empty();
            }));
            return chain.filter(exchange);
        };
    }
}
