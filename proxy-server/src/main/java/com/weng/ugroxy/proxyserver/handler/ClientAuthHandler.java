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
import org.apache.commons.lang3.StringUtils;
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
public class ClientAuthHandler implements ServiceHandler<DefaultProxyMessage<DefaultProxyRequestMessage>> {
    @Override
    public RequestType getSupportTypes() {

        return CLIENT_AUTH_REQUEST;
    }

    @Override
    public RequestType getReturnType() {
        return CLIENT_AUTH_RESPONSE;
    }


    /**
     * 服务将clientKey 对应的 channel存储起来
     * @param ctx
     * @param proxyMessage
     */
    @Override
    public void doService(ChannelHandlerContext ctx, DefaultProxyMessage<DefaultProxyRequestMessage> proxyMessage) {
        try{
            String clientKey = proxyMessage.getData().getClientKey();

            if(StringUtils.isBlank(clientKey)){
                //TODO 优化提示信息
                log.error("clientKey:{} not found ,{}",clientKey,ctx.channel());
                failure(ctx,CLIENT_AUTH_Failure);
                return;
            }

            Channel channel = ProxyChannelManager.getCmdChannel(clientKey);

            if(channel != null){
                //TODO 优化提示信息
                log.error("exist channel for key {}, {}", clientKey, channel);
                failure(ctx,CLIENT_AUTH_Failure);
                return;
            }
            log.info("set clientKey => channel, {}, {}", clientKey, ctx.channel());

            ProxyChannelManager.addCmdChannel(clientKey, ctx.channel());

            success(ctx,CLIENT_AUTH_SUCCESS);

        }catch (IllegalArgumentException e){
            log.info("clientKey:{} auth failed ,{}",proxyMessage.getData().getClientKey(),ctx.channel());
            failure(ctx,CLIENT_AUTH_Failure);
        }
    }
}
