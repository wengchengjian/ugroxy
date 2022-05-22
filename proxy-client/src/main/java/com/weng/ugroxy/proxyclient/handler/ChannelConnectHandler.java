package com.weng.ugroxy.proxyclient.handler;

import com.weng.ugroxy.proxyclient.ClientChannelManager;
import com.weng.ugroxy.proxyclient.listener.NettyProxyChannelBorrowListener;
import com.weng.ugroxy.proxycommon.constants.AttributeKeyEnum;
import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyRequestMessage;
import com.weng.ugroxy.proxycommon.support.Config;
import com.weng.ugroxy.proxycommon.support.handler.ServiceHandler;
import com.weng.ugroxy.proxycommon.utils.SequenceGenerator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.weng.ugroxy.proxycommon.constants.RequestType.CLIENT_CONNECT_REQUEST;
import static com.weng.ugroxy.proxycommon.constants.RequestType.CLIENT_DISCONNECT_REQUEST;

/**
 * @Author 翁丞健
 * @Date 2022/5/13 21:54
 * @Version 1.0.0
 */
@Service
@Slf4j
public class ChannelConnectHandler implements ServiceHandler<DefaultProxyMessage<DefaultProxyRequestMessage>> {


    @Autowired
    @Qualifier("userBootstrap")
    private Bootstrap userBootstrap;

    @Autowired
    @Qualifier("proxyBootstrap")
    private Bootstrap proxyBootstrap;

    @Override
    public RequestType getSupportTypes() {
        return CLIENT_CONNECT_REQUEST;
    }

    @Override
    public RequestType getReturnType() {
        return null;
    }

    @Override
    public void doService(ChannelHandlerContext ctx, DefaultProxyMessage<DefaultProxyRequestMessage> proxyMessage) {

        final String clientKey = proxyMessage.getData().getClientKey();

        String[] serverInfo = new String(proxyMessage.getData().getBody()).split(":");

        String ip = serverInfo[0];

        Integer port = Integer.parseInt(serverInfo[1]);

        // 发送连接请求到代理服务器，让他返回一个动态域名

        Channel cmdChannel = ClientChannelManager.getCmdChannel();

        sendConnectRequest(cmdChannel,proxyMessage).ifPresent(future->{
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        userBootstrap.connect(ip, port).addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if(future.isSuccess()){
                                    Channel userChannel = future.channel();

                                    ClientChannelManager.addRealServerChannel(clientKey,userChannel);
                                }

                            }
                        });
                    }
                }
            });
        });
    }

    private Optional<ChannelFuture> sendConnectRequest(Channel cmdChannel, DefaultProxyMessage<DefaultProxyRequestMessage> message) {
        if(cmdChannel.isWritable()){
            return Optional.of(cmdChannel.writeAndFlush(message));
        }
        return Optional.empty();
    }
}