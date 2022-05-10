package com.weng.ugroxy.proxycommon.protocol.client;

import lombok.Builder;
import lombok.Data;

/**
 * @Author 翁丞健
 * @Date 2022/4/30 21:48
 * @Version 1.0.0
 */
@Data
@Builder
public class ClientProxyMapping {

    private Integer port;

    /** 需要代理的网络信息（代理客户端能够访问），格式 192.168.1.99:80 (必须带端口) */
    private String netInfo;

    private String name;

}
