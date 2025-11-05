package com.polarbookshop.edgeservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

/**
 * @Author: WangZhenqi
 * @Description: 定义测试认证流的泪
 * @Date: Created in 2025-11-05 20:49
 * @Modified By:
 */
@WebFluxTest
// 导入应用的安全性配置
@Import(SecurityConfig.class)
class SecurityConfigTests {

    @Autowired
    WebTestClient webClient;

    // 当检索客户端注册信息时能够跳过与 Keycloak 的交互
    @MockBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    void whenLogoutNotAuthenticatedAndNoCsrfTokenThen403() {
        webClient
                .post()
                .uri("/logout")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenLogoutAuthenticatedAndNoCsrfTokenThen403() {
        webClient
                .mutateWith(SecurityMockServerConfigurers.mockOidcLogin())
                .post()
                .uri("/logout")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenLogoutAuthenticatedAndWithCsrfTokenThen302() {
        when(clientRegistrationRepository.findByRegistrationId("test"))
                .thenReturn(Mono.just(testClientRegistration()));

        webClient
                // 使用 mock ID 令牌来认证用户
                .mutateWith(SecurityMockServerConfigurers.mockOidcLogin())
                // 增强请求以提供所需的 CSRF 令牌
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/logout")
                .exchange()
                // 响应是对 Keycloak 的重定向，以传播退出操作
                .expectStatus().isFound();
    }

    private ClientRegistration testClientRegistration() {
        // Spring Security 所使用的 mock ClientRegistration，以获取联系 keycloak 的 URL
        return ClientRegistration.withRegistrationId("test")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientId("test")
                .authorizationUri("https://sso.polarbookshop.com/auth")
                .tokenUri("https://sso.polarbookshop.com/token")
                .redirectUri("https://polarbookshop.com")
                .build();
    }

}
