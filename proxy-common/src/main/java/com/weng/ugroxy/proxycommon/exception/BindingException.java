package com.weng.ugroxy.proxycommon.exception;

/**
 * @Author 翁丞健
 * @Date 2022/5/11 22:47
 * @Version 1.0.0
 */
public class BindingException extends RuntimeException{
    public BindingException() {
        super();
    }

    public BindingException(String message) {
        super(message);
    }

    public BindingException(String message, Throwable cause) {
        super(message, cause);
    }

    public BindingException(Throwable cause) {
        super(cause);
    }

    protected BindingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
