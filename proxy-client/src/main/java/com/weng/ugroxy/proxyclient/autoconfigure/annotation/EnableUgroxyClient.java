package com.weng.ugroxy.proxyclient.autoconfigure.annotation;

import com.weng.ugroxy.proxyclient.autoconfigure.config.ProxyClientConfiguration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.lang.annotation.*;

/**
 * @Author 翁丞健
 * @Date 2022/5/22 16:54
 * @Version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ProxyClientConfiguration.class)
public @interface EnableUgroxyClient {

}
