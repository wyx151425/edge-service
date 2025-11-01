package com.polarbookshop.edgeservice.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Author: WangZhenqi
 * @Description: 当 Catalog Service 不可用时，配置回退端点
 * @Date: Created in 2025-11-01 21:07
 * @Modified By:
 */
@Configuration
public class WebEndpoints {

    // 函数式 REST 端点是以 bean 的形式定义的
    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        // 提供 Fluent API (翻译为 流式 API 或 链式 API) 来构建路由
        return RouterFunctions.route()
                // 用于处理 GET 端点的回退响应
                .GET("/catalog-fallback", request ->
                        ServerResponse.ok().body(Mono.just(""), String.class))
                // 用于处理 POST 端点的回退响应
                .POST("/catalog-fallback", request ->
                        ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build())
                // 构建函数式端点
                .build();
    }
}
