package com.weng.ugroxy.proxyserver.handler;

import com.weng.ugroxy.proxycommon.constants.AttributeKeyEnum;
import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyRequestMessage;
import com.weng.ugroxy.proxycommon.support.handler.ServiceHandler;
import com.weng.ugroxy.proxyserver.ProxyChannelManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import static com.weng.ugroxy.proxycommon.constants.RequestType.*;
/**
 * @Author 翁丞健
 * @Date 2022/5/1 21:35
 * @Version 1.0.0
 */
public class ChannelDisconnectHandler implements ServiceHandler<DefaultProxyMessage<DefaultProxyRequestMessage>> {
    @Override
    public RequestType getSupportTypes() {
        return CLIENT_DISCONNECT_REQUEST;
    }

    @Override
    public RequestType getReturnType() {
        return CLIENT_DISCONNECT_RESPONSE;
    }

    @Override
    public void doService(ChannelHandlerContext ctx, DefaultProxyMessage<DefaultProxyRequestMessage> proxyMessage) {
        String clientKey = ctx.channel().attr(AttributeKeyEnum.CLIENT_KEY).get();

        if(clientKey==null){
            String userId = proxyMessage.getData().getUri();
            Channel channel = ProxyChannelManager.removeUserChannelFromCmdChannel(ctx.channel(),userId);
            if(channel!=null){
                channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
            return;
        }

        Channel cmdChannel = ProxyChannelManager.getCmdChannel(clientKey);
        if(cmdChannel==null){
            log.error("error cmd channel key {}",clientKey);
            return;
        }

        Channel userChannel = ProxyChannelManager.removeUserChannelFromCmdChannel(cmdChannel, ctx.channel().attr(AttributeKeyEnum.TOKEN).get());
        if (userChannel != null) {
            // 数据发送完成后再关闭连接，解决http1.0数据传输问题
            userChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            ctx.channel().attr(AttributeKeyEnum.NEXT_CHANNEL).remove();
            ctx.channel().attr(AttributeKeyEnum.CLIENT_KEY).remove();
            ctx.channel().attr(AttributeKeyEnum.TOKEN).remove();
        }
    }
}
