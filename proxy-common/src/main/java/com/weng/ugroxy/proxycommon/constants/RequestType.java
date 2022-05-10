package com.weng.ugroxy.proxycommon.constants;

import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyRequestMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author 翁丞健
 * @Date 2022/4/30 18:31
 * @Version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum RequestType {
    /**
     * 客户端请求认证请求包
     */
    CLIENT_AUTH_REQUEST((byte) 1, DefaultProxyRequestMessage.class),

    /**
     * 客户端请求认证响应包
     */
    CLIENT_AUTH_RESPONSE((byte) 2, DefaultProxyResponseMessage.class),
    /**
     * 客户端请求连接请求包
     */
    CLIENT_CONNECT_REQUEST((byte) 3, DefaultProxyRequestMessage.class),
    /**
     * 客户端请求连接响应包
     */
    CLIENT_CONNECT_RESPONSE((byte) 4, DefaultProxyResponseMessage.class),
    /**
     * 客户端请求关闭连接请求包
     */
    CLIENT_DISCONNECT_REQUEST((byte) 5, DefaultProxyRequestMessage.class),

    /**
     * 客户端请求关闭连接响应包
     */
    CLIENT_DISCONNECT_RESPONSE((byte) 6, DefaultProxyResponseMessage.class),

    /**
     * 客户端请求发送数据
     */
    CLIENT_PROXY_TRANSFER_REQUEST((byte) 7, DefaultProxyRequestMessage.class),

    /**
     * 客户端请求发送数据
     */
    CLIENT_PROXY_TRANSFER_RESPONSE((byte) 8, DefaultProxyResponseMessage.class),

    /**
     * 心跳请求包
     */
    HEART_BEAT_REQUEST((byte) 9, DefaultProxyRequestMessage.class),

    /**
     * 心跳响应包
     */
    HEART_BEAT_RESPONSE((byte) 10, DefaultProxyResponseMessage.class);

    private final byte code;

    private final Class<?> clazz;

    public static Class<?> getRequestType(byte code) {
        for (RequestType requestType : RequestType.values()) {
            if (requestType.getCode() == code) {
                return requestType.getClazz();
            }
        }
        return null;
    }

}
