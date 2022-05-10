package com.weng.ugroxy.proxycommon.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 22:53
 * @Version 1.0.0
 */
@Aspect
public class LogAspect {
    @Pointcut("@annotation(com.weng.ugroxy.proxycommon.annotation.MethodLogPoint)")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //TODO 日志切面待实现
        String methodName = proceedingJoinPoint.getSignature().getName();

        return proceedingJoinPoint.proceed();
    }
}
