package com.weng.ugroxy.proxyserver;

import com.weng.ugroxy.proxycommon.container.Container;
import com.weng.ugroxy.proxycommon.exception.BindingException;
import com.weng.ugroxy.proxycommon.protocol.handler.ProxyMessageDecoder;
import com.weng.ugroxy.proxycommon.protocol.handler.ProxyMessageEncoder;
import com.weng.ugroxy.proxyserver.autoconfigure.config.UgroxyServerProperties;
import com.weng.ugroxy.proxyserver.config.ServerProxyConfig;
import com.weng.ugroxy.proxyserver.handler.IdeaCheckHandler;
import com.weng.ugroxy.proxycommon.protocol.handler.ProxySSLhandler;
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
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.Serializable;
import java.util.Set;

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

    @Autowired
    @Qualifier("bootstrap")
    private ServerBootstrap bootstrap;
    @Autowired
    @Qualifier("sslBootstrap")
    private ServerBootstrap sslBootstrap;

    @Autowired
    @Qualifier("userBootstrap")
    private ServerBootstrap userBootstrap;

    @Autowired
    @Qualifier("bossGroup")
    private NioEventLoopGroup bossGroup;

    @Autowired
    @Qualifier("workGroup")
    private NioEventLoopGroup workGroup;

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
            try {
                stop();
            } catch (InterruptedException ex) {
                log.error("proxy server stop failed on {}", ex.getMessage());
            }
        }

        if(ugroxyServerProperties.getSsl().isEnabled()){
           initSSLTCPTransport();
        }

        startUserPort();


    }

    private void startUserPort() {
        userBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(bytesMetricsHandler);
                pipeline.addLast(userChannelHandler);
            }
        });

        Set<Integer> ports = ServerProxyConfig.getInstance().getUserPorts();

        ports.forEach(port -> {
            try {
                userBootstrap.bind(port).sync().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if(future.isSuccess()){
                            log.info("user proxy server bind port  success on {}", port);
                        }else{
                            log.info("user proxy server bind port  success on {}", port);
                        }
                    }
                });
            } catch (InterruptedException e) {
                throw new BindingException(String.format("user proxy server bind port  failed on %d", port));
            }
        });
    }

    private void initSSLTCPTransport() {
        // 初始化
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
            try {
                stop();
            } catch (InterruptedException ex) {
                log.error("proxy server stop failed on {}", ex.getMessage());
            }
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
    public void stop() throws InterruptedException {
        bossGroup.shutdownGracefully().sync();
        workGroup.shutdownGracefully().sync();
    }

    @Override
    public void reset() {
        try{
            stop();
            start();
        }catch (InterruptedException e) {
            log.error("reset proxy server failed on {}", e.getMessage());
        }
    }

    @Override
    public void onChange() {

    }

}
