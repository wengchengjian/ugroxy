package com.weng.ugroxy.proxycommon.exception;

/**
 * @Author 翁丞健
 * @Date 2022/4/29 21:31
 * @Version 1.0.0
 */
public class NotSupportedVersionException extends RuntimeException{
    public NotSupportedVersionException() {
        super();
    }

    public NotSupportedVersionException(String message) {
        super(message);
    }

    public NotSupportedVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportedVersionException(Throwable cause) {
        super(cause);
    }

    protected NotSupportedVersionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
