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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
    @Qualifier("bootstrap")
    private Bootstrap bootstrap;

    @Autowired
    @Qualifier("proxyBootstrap")
    private Bootstrap proxyBootstrap;

    @Override
    public RequestType[] getSupportTypes() {
        return new RequestType[]{CLIENT_CONNECT_REQUEST};
    }

    @Override
    public void doService(ChannelHandlerContext ctx, DefaultProxyMessage<DefaultProxyRequestMessage> proxyMessage) {


        final Channel  cmdChannel = ctx.channel();

        final String userId = proxyMessage.getData().getUri();

        String[] serverInfo = new String(proxyMessage.getData().getBody()).split(":");

        String ip = serverInfo[0];

        Integer port = Integer.parseInt(serverInfo[1]);

        bootstrap.connect(ip, port).addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {

                // 连接后端服务器成功
                if (future.isSuccess()) {
                    final Channel realServerChannel = future.channel();
                    log.debug("connect realserver success, {}", realServerChannel);

                    realServerChannel.config().setOption(ChannelOption.AUTO_READ, false);

                    // 获取连接
                    ClientChannelManager.borrowProxyChannel(proxyBootstrap, new NettyProxyChannelBorrowListener() {

                        @Override
                        public void success(Channel channel) {
                            // 连接绑定
                            channel.attr(AttributeKeyEnum.NEXT_CHANNEL).set(realServerChannel);
                            realServerChannel.attr(AttributeKeyEnum.NEXT_CHANNEL).set(channel);

                            DefaultProxyRequestMessage requestMessage = DefaultProxyRequestMessage.builder()
                                    .seqId(SequenceGenerator.next())
                                    .body(null)
                                    .uri(userId + "@" + Config.getInstance().getStringProperty("client.key","ugroxy"))
                                    .build();
                            // 远程绑定
                            DefaultProxyMessage proxyMessage = DefaultProxyMessage.getDefaultMessage(requestMessage, CLIENT_CONNECT_REQUEST.getCode());
                            channel.writeAndFlush(proxyMessage);

                            realServerChannel.config().setOption(ChannelOption.AUTO_READ, true);
                            ClientChannelManager.addRealServerChannel(userId, realServerChannel);
                            ClientChannelManager.setRealServerChannelToken(realServerChannel, userId);
                        }

                        @Override
                        public void error(Throwable cause) {
                            DefaultProxyRequestMessage requestMessage = DefaultProxyRequestMessage.builder()
                                    .seqId(SequenceGenerator.next())
                                    .body(null)
                                    .uri(userId)
                                    .build();
                            // 远程绑定 断开连接
                            DefaultProxyMessage proxyMessage = DefaultProxyMessage.getDefaultMessage(requestMessage, CLIENT_DISCONNECT_REQUEST.getCode());
                            cmdChannel.writeAndFlush(proxyMessage);
                        }
                    });

                } else {
                    DefaultProxyRequestMessage requestMessage = DefaultProxyRequestMessage.builder()
                            .seqId(SequenceGenerator.next())
                            .body(null)
                            .uri(userId)
                            .build();
                    // 远程绑定 断开连接
                    DefaultProxyMessage proxyMessage = DefaultProxyMessage.getDefaultMessage(requestMessage, CLIENT_DISCONNECT_REQUEST.getCode());
                    cmdChannel.writeAndFlush(proxyMessage);
                }
            }
        });
    }
}