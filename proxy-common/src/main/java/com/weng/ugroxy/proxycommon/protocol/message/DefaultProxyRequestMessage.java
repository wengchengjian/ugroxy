package com.weng.ugroxy.proxycommon.protocol.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 翁丞健
 * @Date 2022/4/29 21:47
 * @Version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultProxyRequestMessage {
    /**
     * 每个消息都有一个唯一的id
     */
    private Long seqId;

    /**
     * 消息请求的源地址
     */
    private String uri;
//
//    private String queryParms;

    private byte[] body;


}
