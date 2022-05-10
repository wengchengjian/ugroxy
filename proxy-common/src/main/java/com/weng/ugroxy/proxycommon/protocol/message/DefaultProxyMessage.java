package com.weng.ugroxy.proxycommon.protocol.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.weng.ugroxy.proxycommon.constants.CodecEnum.KYRO;
import static com.weng.ugroxy.proxycommon.constants.CompressEnum.GZIP;
import static com.weng.ugroxy.proxycommon.constants.RequestType.CLIENT_AUTH_RESPONSE;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 21:37
 * @Version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefaultProxyMessage<T> implements Serializable,Cloneable {

    /**
     * 消息类型
     */
    private byte type;

    /**
     * 编码格式
     */
    private byte codec;

    /**
     * 消息压缩格式
     */
    private byte compress;

    /**
     * 消息请求体
     */
    private T data;

    public static  <U> DefaultProxyMessage<U> getDefaultMessage(U data){
        return DefaultProxyMessage.<U>builder()
                .type(CLIENT_AUTH_RESPONSE.getCode())
                .codec(KYRO.getCode())
                .compress(GZIP.getCode())
                .data(data)
                .build();

    }


}
