package com.weng.ugroxy.proxycommon.protocol.client;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author 翁丞健
 * @Date 2022/4/30 21:46
 * @Version 1.0.0
 */
@Data
@Builder
public class Client implements Serializable {
    private static final long serialVersionUID = -824180782780687096L;

    private String client;

    private String clientKey;

    private List<ClientProxyMapping> proxyMappings;

    private byte status;


}
