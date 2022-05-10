package com.weng.ugroxy.proxycommon.exception;

/**
 * @Author 翁丞健
 * @Date 2022/4/29 21:30
 * @Version 1.0.0
 */
public class NotSupportProtocolException extends RuntimeException{
    public NotSupportProtocolException() {
        super();
    }

    public NotSupportProtocolException(String message) {
        super(message);
    }

    public NotSupportProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportProtocolException(Throwable cause) {
        super(cause);
    }

    protected NotSupportProtocolException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
