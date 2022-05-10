package com.weng.ugroxy.proxycommon.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 21:22
 * @Version 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
@Inherited
public @interface Handler {
    String value() default "";
}
