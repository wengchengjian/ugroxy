package com.weng.ugroxy.proxyserver;

import com.weng.ugroxy.proxycommon.container.Container;
import com.weng.ugroxy.proxycommon.protocol.handler.ProxyMessageDecoder;
import com.weng.ugroxy.proxycommon.protocol.handler.ProxyMessageEncoder;
import com.weng.ugroxy.proxyserver.autoconfigure.config.UgroxyServerProperties;
import com.weng.ugroxy.proxyserver.config.ServerProxyConfig;
import com.weng.ugroxy.proxyserver.handler.IdeaCheckHandler;
import com.weng.ugroxy.proxyserver.handler.ProxySSLhandler;
import com.weng.ugroxy.proxyserver.handler.ServerHandlerDispatcher;
import com.weng.ugroxy.proxyserver.handler.UserChannelHandler;
import com.weng.ugroxy.proxyserver.handler.metrics.handler.BytesMetricsHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Author 翁丞健
 * @Date 2022/4/25 23:03
 * @Version 1.0.0
 */
@Slf4j
public class NettyProxyServerContainer implements Container, Serializable {

    private static final long serialVersionUID = -8586927507516656849L;

    @Autowired
    private IdeaCheckHandler ideaCheckHandler;

    @Autowired
    private ProxySSLhandler proxySSLhandler;

    @Autowired
    private UgroxyServerProperties ugroxyServerProperties;

    @Autowired
    private ProxyMessageDecoder proxyMessageDecoder;

    @Autowired
    private ProxyMessageEncoder proxyMessageEncoder;

    @Autowired
    private ServerHandlerDispatcher serverHandlerDispatcher;

    @Autowired
    private BytesMetricsHandler bytesMetricsHandler;

    @Autowired
    private UserChannelHandler userChannelHandler;

    private ServerBootstrap bootstrap;

    private ServerBootstrap sslBootstrap;

    private ServerBootstrap userBootstrap;


    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workGroup;


    public NettyProxyServerContainer(){
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
    }

    @Override
    public void start() {
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        addHandler(pipeline);
                    }
                });
        try {
            bootstrap.bind(ugroxyServerProperties.getHost(), ugroxyServerProperties.getPort()).sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("proxy server start success on {}", future.channel().localAddress());
                    } else {
                        log.error("proxy server start failed on {}", future.channel().localAddress());
                    }
                }
            });
        } catch (InterruptedException e) {
            log.error("proxy server start failed on {}", e.getMessage());
            stop();
        }

        if(ugroxyServerProperties.getSsl().isEnabled()){
           initSSLTCPTransport();
        }

        startUserPort();


    }

    private void startUserPort() {
        if(userBootstrap == null){
            userBootstrap = new ServerBootstrap();
        }
        userBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(bytesMetricsHandler);
                pipeline.addLast(userChannelHandler);
            }
        })
    }

    private void initSSLTCPTransport() {
        // 初始化
        sslBootstrap = new ServerBootstrap();
        sslBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("sslContainer",proxySSLhandler);
                addHandler(pipeline);
            }
        });

        try {
            bootstrap.bind(ugroxyServerProperties.getHost(), ugroxyServerProperties.getPort()).sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("ssl proxy server start success on {}", future.channel().localAddress());
                    } else {
                        log.error("ssl proxy server start failed on {}", future.channel().localAddress());
                    }
                }
            });
        } catch (InterruptedException e) {
            log.error("ssl proxy server start failed on {}", e.getMessage());
            stop();
        }
    }

    private void addHandler(ChannelPipeline pipeline) {
        // 添加常规处理器
        pipeline.addLast(proxyMessageEncoder);
        pipeline.addLast(proxyMessageDecoder);
        pipeline.addLast(ideaCheckHandler);
        pipeline.addLast(serverHandlerDispatcher);

    }

    @Override
    public void stop() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void onChange() {

    }

    @Override
    public void close() throws IOException {

    }
}
