package com.polarbookshop.edgeservice.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Author: WangZhenqi
 * @Description: 返回当前已认证用户的信息
 * @Date: Created in 2025-11-05 20:05
 * @Modified By:
 */
@RestController
public class UserController {

//    @GetMapping("user")
//    public Mono<User> getUser() {
//        // 从 ReactiveSecurityContextHolder 中获取当前认证用户的 SecurityContext
//        return ReactiveSecurityContextHolder.getContext()
//                // 从 SecurityContext 中获取 Authentication
//                .map(SecurityContext::getAuthentication)
//                .map(authentication ->
//                        // 从 Authentication 中获取 principal。对于 OIDC 来说，这是 OidcUser 类型的对象
//                        (OidcUser) authentication.getPrincipal())
//                // 使用来自 OidcUser (从 ID 令牌中抽取的) 的数据构建 User 对象
//                .map(oidcUser ->
//                        new User(
//                                oidcUser.getPreferredUsername(),
//                                oidcUser.getGivenName(),
//                                oidcUser.getFamilyName(),
//                                List.of("employee", "customer")
//                        )
//                );
//    }

    @GetMapping("user")
    public Mono<User> getUser(
            // 基于 OidcUser 中包含的相关 claim 构建 User 对象
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        // 基于 OidcUser 中包含的相关 claim 构建 User 对象
        var user = new User(
                oidcUser.getPreferredUsername(),
                oidcUser.getGivenName(),
                oidcUser.getFamilyName(),
                List.of("employee", "customer")
        );
        // 鉴于 Edge Service 是一个反应式应用，将 User 对象包装到反应式发布者中
        return Mono.just(user);
    }
}
