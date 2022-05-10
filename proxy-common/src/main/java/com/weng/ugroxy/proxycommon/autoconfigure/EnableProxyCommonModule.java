package com.weng.ugroxy.proxycommon.autoconfigure;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.*;

/**
 * TODO JPA注入repository时需要自定义注入逻辑，需要根据配置(ugroxy.logging.storaged)来判断是否需要注入
 * @Author 翁丞健
 * @Date 2022/4/26 22:14
 * @Version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@EnableJpaAuditing
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableCaching
@EnableConfigurationProperties(ProxyCommonProperties.class)
@EnableJpaRepositories(basePackages = "com.weng.ugroxy.proxycommon.repository")
@Import(ProxyCommonAutoConfiguration.class)
public @interface EnableProxyCommonModule {

}
