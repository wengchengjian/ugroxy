package com.weng.ugroxy.proxyclient.listener;

import io.netty.channel.Channel;

/**
 * @Author 翁丞健
 * @Date 2022/5/12 21:56
 * @Version 1.0.0
 */
public interface NettyProxyChannelBorrowListener {
    void success(Channel channel);

    void error(Throwable cause);
}
