package com.weng.ugroxy.proxyclient;

import com.weng.ugroxy.proxyclient.handler.ProxyClientChannelHandler;
import com.weng.ugroxy.proxyclient.handler.RealServerChannelHandler;
import com.weng.ugroxy.proxycommon.container.Container;
import com.weng.ugroxy.proxycommon.protocol.handler.ProxyMessageDecoder;
import com.weng.ugroxy.proxycommon.protocol.handler.ProxyMessageEncoder;
import com.weng.ugroxy.proxycommon.protocol.handler.ProxySSLhandler;
import com.weng.ugroxy.proxycommon.support.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.IOException;

/**
 * @Author 翁丞健
 * @Date 2022/5/11 22:55
 * @Version 1.0.0
 */
@Component
@Slf4j
public class NettyProxyClientContainer implements Container {


    private NioEventLoopGroup workerGroup;

    private Bootstrap bootstrap;

    private Bootstrap realServerBootStrap;

    private Config config = Config.getInstance();
    @Autowired
    private RealServerChannelHandler realServerChannelHandler;

    @Autowired
    private ProxyMessageDecoder proxyMessageDecoder;

    @Autowired
    private ProxyMessageEncoder proxyMessageEncoder;

    @Autowired
    private ProxyClientChannelHandler proxyClientChannelHandler;

    @Autowired
    private ProxySSLhandler proxySSLhandler;

    private SSLContext sslContext;

    public NettyProxyClientContainer(){
        workerGroup = new NioEventLoopGroup();

        realServerBootStrap = new Bootstrap();

        realServerBootStrap.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(realServerChannelHandler);
            }
        });

        bootstrap = new Bootstrap();

        bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(proxySSLhandler);
                pipeline.addLast(proxyMessageDecoder);
                pipeline.addLast(proxyMessageEncoder);
                pipeline.addLast(proxyClientChannelHandler);
            }
        });
    }

    @Override
    public void start() {
        connectProxyServer();
    }

    private void connectProxyServer() {
        bootstrap.connect(config.getStringProperty("server.host","127.0.0.1"), config.getIntProperty("server.port",)).addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {

                    // 连接成功，向服务器发送客户端认证信息（clientKey）
                    ClientChannelMannager.setCmdChannel(future.channel());
                    ProxyMessage proxyMessage = new ProxyMessage();
                    proxyMessage.setType(ProxyMessage.C_TYPE_AUTH);
                    proxyMessage.setUri(config.getStringValue("client.key"));
                    future.channel().writeAndFlush(proxyMessage);
                    sleepTimeMill = 1000;
                    logger.info("connect proxy server success, {}", future.channel());
                } else {
                    logger.warn("connect proxy server failed", future.cause());

                    // 连接失败，发起重连
                    reconnectWait();
                    connectProxyServer();
                }
            }
        });
    }

    @Override
    public void stop() throws InterruptedException {

    }

    @Override
    public void reset() {

    }

    @Override
    public void onChange() {

    }

}
