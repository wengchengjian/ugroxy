package com.weng.ugroxy.proxycommon.utils;

import com.weng.ugroxy.proxycommon.constants.StatusCode;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyResponseMessage;

/**
 * @Author 翁丞健
 * @Date 2022/5/1 18:35
 * @Version 1.0.0
 */
public class Result {

    public static DefaultProxyResponseMessage of(StatusCode statusCode, Object ret) {
        return new DefaultProxyResponseMessage(SequenceGenerator.next(),null,statusCode.getCode(),statusCode.getMsg(),ret);
    }
}
