package com.weng.ugroxy.proxyserver.handler.metrics;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author 翁丞健
 * @Date 2022/5/10 22:20
 * @Version 1.0.0
 */
@Data
@Builder
public class Metrics implements Serializable {
    private static final long serialVersionUID = 1L;

    private int port;

    private long readBytes;

    private long writeBytes;

    private long readMsgs;

    private long wroteMsgs;

    private long channels;

    private long timestamp;
}
