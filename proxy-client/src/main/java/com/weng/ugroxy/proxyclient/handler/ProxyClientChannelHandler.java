package com.weng.ugroxy.proxyclient.handler;

import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author 翁丞健
 * @Date 2022/5/11 23:08
 * @Version 1.0.0
 */
@Slf4j
@Service
public class ProxyClientChannelHandler extends SimpleChannelInboundHandler<DefaultProxyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultProxyMessage msg) throws Exception {

    }
}
