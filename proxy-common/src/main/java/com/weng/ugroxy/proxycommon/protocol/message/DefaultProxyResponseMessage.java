package com.weng.ugroxy.proxycommon.protocol.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 翁丞健
 * @Date 2022/4/29 21:47
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultProxyResponseMessage<T> {
    /**
     * 每个消息都有一个唯一的流水id
     */
    private Long seqId;

    /**
     * 消息请求的源地址
     */
    private String uri;

    /**
     * 消息的状态码
     */
    private Integer statusCode;

    /**
     * 消息的状态信息
     */
    private String statusMessage;

    /**
     * 消息的响应体
     */
    private T body;


}
