package com.polarbookshop.edgeservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * @Author: WangZhenqi
 * @Description: 使用 Redis 容器来测试 Spring 上下文的加载
 * @Date: Created in 2025-11-01 14:10
 * @Modified By:
 */
// 加载完整的 Spring Web 应用上下文以及监听随机端口的 Web 环境
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// 激活测试容器的自动启动和清理
@Testcontainers
class EdgeServiceApplicationTests {

    private static final int REDIS_PORT = 6379;

    // 定义用于测试的 Redis 容器
    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2"))
            .withExposedPorts(REDIS_PORT);

    // 重写 Redis 配置，以指向用于测试的 Redis 实例
    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", () -> redis.getHost());
        registry.add("spring.redis.port", () -> redis.getMappedPort(REDIS_PORT));
    }

    @Test
    void contextLoads() {
    }

    // 用于验证应用上下文能够正确加载以及到 Redis 的连接能够成功建立的空测试方法
    @Test
    void verifyThatSpringContextLoads() {
    }
}
