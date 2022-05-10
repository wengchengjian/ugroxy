package com.weng.ugroxy.proxycommon.exception;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 21:57
 * @Version 1.0.0
 */
public class SerializeException extends RuntimeException{
    public SerializeException() {
        super();
    }

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }

    protected SerializeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
