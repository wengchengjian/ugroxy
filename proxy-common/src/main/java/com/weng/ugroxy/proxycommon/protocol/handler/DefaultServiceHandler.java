package com.weng.ugroxy.proxycommon.protocol.handler;

import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.support.handler.ServiceHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 *  默认的服务处理器，支持所有请求处理，作为兜底处理器，返回默认处理结果
 * @Author 翁丞健
 * @Date 2022/4/29 23:55
 * @Version 1.0.0
 */
@Service
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultServiceHandler implements ServiceHandler<DefaultProxyMessage> {

    @Override
    public RequestType[] getSupportTypes() {
        // 返回所有支持的请求类型,默认为所有请求类型，为
        return RequestType.values();
    }

    @Override
    public void doService(ChannelHandlerContext ctx, DefaultProxyMessage proxyMessage) {

    }
}
