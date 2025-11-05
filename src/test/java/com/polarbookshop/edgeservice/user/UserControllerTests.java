package com.polarbookshop.edgeservice.user;

import com.polarbookshop.edgeservice.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Author: WangZhenqi
 * @Description: 定义类来测试 UserController 的安全策略
 * @Date: Created in 2025-11-05 20:42
 * @Modified By:
 */
@WebFluxTest(UserController.class)
// 导入应用的安全性配置
@Import(SecurityConfig.class)
class UserControllerTests {

    @Autowired
    WebTestClient webClient;

    // 当检索客户端注册信息时能够跳过与 Keycloak 的交互
    @MockBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

    // 校验请求在未经认证时返回 HTTP 401 响应
    @Test
    void whenNotAuthenticatedThen401() {
        webClient
                .get()
                .uri("/user")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenAuthenticatedThenReturnUser() {
        // 预期的认证用户
        var expectedUser = new User("jon.snow", "Jon", "Snow", List.of("employee", "customer"));

        webClient
                // 基于 OIDC 和预期用户定义认证上下文
                .mutateWith(configureMockOidcLogin(expectedUser))
                .get()
                .uri("/user")
                .exchange()
                .expectStatus().is2xxSuccessful()
                // 预期获得与当前认证用户具有相同信息的 User 对象
                .expectBody(User.class)
                .value(user -> assertThat(user).isEqualTo(expectedUser));
    }

    private SecurityMockServerConfigurers.OidcLoginMutator configureMockOidcLogin(User expectedUser) {
        return SecurityMockServerConfigurers.mockOidcLogin().idToken(
                // 构建 mock ID 令牌
                builder -> {
                    builder.claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.username());
                    builder.claim(StandardClaimNames.GIVEN_NAME, expectedUser.firstName());
                    builder.claim(StandardClaimNames.FAMILY_NAME, expectedUser.lastName());
                });
    }

}
