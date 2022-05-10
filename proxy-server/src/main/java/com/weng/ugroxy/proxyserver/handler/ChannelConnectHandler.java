package com.weng.ugroxy.proxyserver.handler;

import com.weng.ugroxy.proxycommon.constants.AttributeKeyEnum;
import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyRequestMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyResponseMessage;
import com.weng.ugroxy.proxycommon.support.handler.ServiceHandler;
import com.weng.ugroxy.proxyserver.ProxyChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;

import static com.weng.ugroxy.proxycommon.constants.RequestType.*;
/**
 * @Author 翁丞健
 * @Date 2022/5/1 17:54
 * @Version 1.0.0
 */
@Service
@Slf4j
public class ChannelConnectHandler implements ServiceHandler<DefaultProxyMessage<DefaultProxyRequestMessage>> {
    @Override
    public RequestType[] getSupportTypes() {
        return new RequestType[]{CLIENT_CONNECT_REQUEST};
    }

    @Override
    public void doService(ChannelHandlerContext ctx, DefaultProxyMessage<DefaultProxyRequestMessage> proxyMessage) {
        log.info("收到客户端连接请求");
        String uri = proxyMessage.getData().getUri();

        if(StringUtils.isEmpty(uri)){
            log.error("客户端连接请求uri为空");
            failure(ctx, "客户端连接请求uri为空");
            return;
        }

        String[] tokens = uri.split("@");

        if(tokens.length != 2){
            failure(ctx, "客户端连接请求uri格式错误");
            log.error("客户端连接请求uri格式错误");
            return;
        }

        Channel cmdChannel = ProxyChannelManager.getCmdChannel(tokens[1]);
        if (cmdChannel == null) {
            failure(ctx,"ConnectMessage:error cmd channel key"+tokens[1]);
            log.warn("ConnectMessage:error cmd channel key {}", tokens[1]);
            return;
        }

        Channel userChannel = ProxyChannelManager.getUserChannel(cmdChannel, tokens[0]);

        if(userChannel!=null){
            ctx.channel().attr(AttributeKeyEnum.TOKEN).set(tokens[0]);
            ctx.channel().attr(AttributeKeyEnum.CLIENT_KEY).set(tokens[1]);
            ctx.channel().attr(AttributeKeyEnum.NEXT_CHANNEL).set(userChannel);

            userChannel.attr(AttributeKeyEnum.NEXT_CHANNEL).set(ctx.channel());
            // 代理客户端与后端服务器连接成功，修改用户连接为可读状态
            userChannel.config().setOption(ChannelOption.AUTO_READ, true);
        }

    }
}
