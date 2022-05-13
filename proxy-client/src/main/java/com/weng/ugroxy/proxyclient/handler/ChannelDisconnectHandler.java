package com.weng.ugroxy.proxyclient.handler;

import com.weng.ugroxy.proxyclient.ClientChannelManager;
import com.weng.ugroxy.proxycommon.constants.AttributeKeyEnum;
import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyRequestMessage;
import com.weng.ugroxy.proxycommon.support.handler.ServiceHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

import static com.weng.ugroxy.proxycommon.constants.RequestType.CLIENT_DISCONNECT_REQUEST;

/**
 * @Author 翁丞健
 * @Date 2022/5/13 21:54
 * @Version 1.0.0
 */
@Service
public class ChannelDisconnectHandler implements ServiceHandler<DefaultProxyMessage<DefaultProxyRequestMessage>> {
    @Override
    public RequestType[] getSupportTypes() {
        return new RequestType[]{CLIENT_DISCONNECT_REQUEST};
    }

    @Override
    public void doService(ChannelHandlerContext ctx, DefaultProxyMessage<DefaultProxyRequestMessage> proxyMessage) {
        Channel realServerChannel = ctx.channel().attr(AttributeKeyEnum.NEXT_CHANNEL).get();
        log.debug("handleDisconnectMessage, {}", realServerChannel);

        if (realServerChannel != null) {
            ctx.channel().attr(AttributeKeyEnum.NEXT_CHANNEL).remove();
            ClientChannelManager.returnProxyChannel(ctx.channel());

            //等待客户端消息发送完毕
            if(realServerChannel.isActive()){
                realServerChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }else{
                realServerChannel.close();
            }
        }
    }
}