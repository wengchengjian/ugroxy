package com.weng.ugroxy.proxyserver.handler;

import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyRequestMessage;
import com.weng.ugroxy.proxycommon.support.handler.ServiceHandler;
import com.weng.ugroxy.proxyserver.ProxyChannelManager;
import com.weng.ugroxy.proxyserver.config.ServerProxyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import static com.weng.ugroxy.proxycommon.constants.StatusCode.*;
import java.util.List;

import static com.weng.ugroxy.proxycommon.constants.RequestType.*;

/**
 * @Author 翁丞健
 * @Date 2022/4/29 23:13
 * @Version 1.0.0
 */
@Service
@Slf4j
public class ClientAuthServiceHandler implements ServiceHandler<DefaultProxyMessage<DefaultProxyRequestMessage>> {
    @Override
    public RequestType[] getSupportTypes() {

        return new RequestType[]{CLIENT_AUTH_REQUEST};
    }

    @Override
    public void doService(ChannelHandlerContext ctx, DefaultProxyMessage<DefaultProxyRequestMessage> proxyMessage) {
        try{
            String clientKey = proxyMessage.getData().getUri();

            List<Integer> ports = ServerProxyConfig.getInstance().getClientInetPorts(clientKey);

            if(CollectionUtils.isEmpty(ports)){
                log.error("clientKey:{} not found ,{}",clientKey,ctx.channel());
                failure(ctx,CLIENT_AUTH_Failure);
                return;
            }

            Channel channel = ProxyChannelManager.getCmdChannel(clientKey);

            if(channel != null){
                log.error("exist channel for key {}, {}", clientKey, channel);
                failure(ctx,CLIENT_AUTH_Failure);
                return;
            }
            log.info("set port => channel, {}, {}, {}", clientKey, ports, ctx.channel());

            ProxyChannelManager.addCmdChannel(ports,clientKey, ctx.channel());

            success(ctx,CLIENT_AUTH_SUCCESS);

        }catch (IllegalArgumentException e){
            log.info("clientKey:{} auth failed ,{}",proxyMessage.getData().getUri(),ctx.channel());
            failure(ctx,CLIENT_AUTH_Failure);
        }
    }
}
