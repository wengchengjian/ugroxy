package com.weng.ugroxy.proxyserver.handler;

import com.weng.ugroxy.proxycommon.constants.AttributeKeyEnum;
import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyRequestMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyResponseMessage;
import com.weng.ugroxy.proxycommon.support.handler.ServiceHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static com.weng.ugroxy.proxycommon.constants.StatusCode.*;
import static com.weng.ugroxy.proxycommon.constants.RequestType.*;
import static com.weng.ugroxy.proxycommon.constants.AttributeKeyEnum.*;
/**
 * @Author 翁丞健
 * @Date 2022/5/1 21:53
 * @Version 1.0.0
 */
@Service
@Slf4j
public class ProxyTransforDataHandler implements ServiceHandler<DefaultProxyMessage> {
    @Override
    public RequestType getSupportTypes() {
        return CLIENT_PROXY_TRANSFER_REQUEST;
    }

    @Override
    public RequestType getReturnType() {
        return CLIENT_PROXY_TRANSFER_RESPONSE;
    }

    @Override
    public void doService(ChannelHandlerContext ctx, DefaultProxyMessage proxyMessage) {
        Channel userChannel = ctx.channel().attr(NEXT_CHANNEL).get();
        if (userChannel == null) {
            //TODO 待优化
            log.error("用户连接已断开：{}", ctx.channel());
            failure(ctx,CLIENT_TRANSFER_DATA_FAILURE);
            return;
        }
        userChannel.writeAndFlush(proxyMessage);

    }
}
