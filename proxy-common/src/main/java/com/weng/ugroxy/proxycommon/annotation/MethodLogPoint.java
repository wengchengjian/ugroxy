package com.weng.ugroxy.proxycommon.annotation;

import java.lang.annotation.*;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 22:24
 * @Version 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MethodLogPoint {

}
