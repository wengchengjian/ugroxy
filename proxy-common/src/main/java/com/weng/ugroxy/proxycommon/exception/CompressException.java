package com.weng.ugroxy.proxycommon.exception;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 21:55
 * @Version 1.0.0
 */
public class CompressException extends RuntimeException{
    public CompressException() {
        super();
    }

    public CompressException(String message) {
        super(message);
    }

    public CompressException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompressException(Throwable cause) {
        super(cause);
    }

    protected CompressException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
