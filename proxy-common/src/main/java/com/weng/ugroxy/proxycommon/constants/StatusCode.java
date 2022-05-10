package com.weng.ugroxy.proxycommon.constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author 翁丞健
 * @Date 2022/5/1 18:37
 * @Version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum StatusCode {
    /**
     * 请求成功
     */
    SUCCESS(200, "成功"),

    CLIENT_AUTH_SUCCESS(201,"客户端认证成功"),
    CLIENT_AUTH_Failure(101,"客户端认证失败"),

    CLIENT_TRANSFER_DATA_SUCCESS(202,"数据传输成功"),

    CLIENT_TRANSFER_DATA_Failure(102,"数据传输失败"),
    /**
     * 请求失败
     */
    Failure(100, "失败"),

    ;
    private final Integer code;

    private final String msg;
}
