package com.weng.ugroxy.proxyserver.autoconfigure.annotation;

import com.weng.ugroxy.proxyserver.autoconfigure.config.UgroxyServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

/**
 * @Author 翁丞健
 * @Date 2022/5/5 21:53
 * @Version 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties(UgroxyServerProperties.class)
@ComponentScan(basePackages = "com.weng.ugroxy.proxyserver.handler")
public @interface EnableUgroxyServer {

}
