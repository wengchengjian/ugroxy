package com.weng.ugroxy.proxyclient;

import com.weng.ugroxy.proxyclient.autoconfigure.properties.UgroxyClientProperties;
import com.weng.ugroxy.proxyclient.handler.ProxyClientChannelHandler;
import com.weng.ugroxy.proxyclient.handler.RealServerChannelHandler;
import com.weng.ugroxy.proxycommon.constants.CodecEnum;
import com.weng.ugroxy.proxycommon.constants.CompressEnum;
import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.container.Container;
import com.weng.ugroxy.proxycommon.exception.RetryException;
import com.weng.ugroxy.proxycommon.protocol.handler.ProxyMessageDecoder;
import com.weng.ugroxy.proxycommon.protocol.handler.ProxyMessageEncoder;
import com.weng.ugroxy.proxycommon.protocol.handler.ProxySSLhandler;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyRequestMessage;
import com.weng.ugroxy.proxycommon.support.Config;
import com.weng.ugroxy.proxycommon.utils.SequenceGenerator;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @Author 翁丞健
 * @Date 2022/5/11 22:55
 * @Version 1.0.0
 */
@Slf4j
public class NettyProxyClientContainer implements Container {

    //TODO 使用一个bootstrap还是多个有待思考
    @Autowired
    @Qualifier("workerGroup")
    private NioEventLoopGroup workerGroup;

    @Autowired
    @Qualifier("userBootstrap")
    private Bootstrap userBootstrap;

    @Autowired
    @Qualifier("proxyBootstrap")
    private Bootstrap proxyBootstrap;

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

    @Autowired
    private UgroxyClientProperties ugroxyClientProperties;


    private SSLContext sslContext;

    public NettyProxyClientContainer(){
        // 初始化用户本地bootstrap
        userBootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(realServerChannelHandler);
            }
        });
        // 初始化代理客户端bootstrap
        proxyBootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>() {
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

    private static  Integer TIMES = 0;

    private static Integer waitTime = 0;

    private synchronized void connectServer() {
        // 连接到代理服务端
        connectProxyServer();

        // 连接到用户需要代理的服务端
        connectUserServer();
    }

    private synchronized void connectProxyServer(){
        try {
            // 等待重试时间,时间逐渐递增
            waitRetryTime();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(TIMES < ugroxyClientProperties.getRetry().getTimes()){
            TIMES++;
            log.info("正在进行第{}次连接重试",TIMES);
        }else{
            throw new RetryException("重试次数已达上限");
        }

        proxyBootstrap.connect(ugroxyClientProperties.getHost(), ugroxyClientProperties.getPort()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    Channel cmdChannel = future.channel();
                    // 设置与代理服务器相连的channel
                    ClientChannelManager.setCmdChannel(cmdChannel);

                    // 连接成功，向服务器发送客户端认证信息（clientKey）
                    authToServer(cmdChannel);

                    log.info("connect proxy server success, {}", future.channel());
                } else {
                    log.warn("connect proxy server failed", future.cause());
                    connectProxyServer();
                }
            }
        });
    }

    private synchronized void connectUserServer(){

        for (UgroxyClientProperties.Proxy proxy : ugroxyClientProperties.getProxyList()) {
            userBootstrap.connect(proxy.getHost(), proxy.getPort()).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        Channel userChannel = future.channel();
                        // 设置与代理服务器相连的channel
                        ClientChannelManager.addRealServerChannel(proxy.getClientKey(),userChannel);

                        log.info("connect user server success, {}", future.channel().localAddress());
                    } else {
                        log.warn("connect user server failed: {}", future.channel().localAddress());
                    }
                }
            });
        }

    }

    private void waitRetryTime() throws InterruptedException {
        log.info("等待重试时间：{}ms",waitTime);
        TimeUnit.MILLISECONDS.sleep(waitTime);
        incrementWaitTime();
    }

    private synchronized void incrementWaitTime() {
        int increment = ugroxyClientProperties.getRetry().getWaitTime();

        int maxWaitTime = ugroxyClientProperties.getRetry().getMaxTime();

        if(waitTime+increment < maxWaitTime){
            waitTime += increment;
        }else{
            waitTime = maxWaitTime;
        }
    }

    public void authToServer(Channel channel){
        DefaultProxyRequestMessage message = DefaultProxyRequestMessage.builder()
                .body(null).seqId(SequenceGenerator.next()).clientKey(ugroxyClientProperties.getClientKey()).build();

        DefaultProxyMessage<DefaultProxyRequestMessage> proxyMessage = DefaultProxyMessage.getDefaultMessage(message,RequestType.CLIENT_AUTH_REQUEST.getCode());
        channel.writeAndFlush(proxyMessage);
    }

    @Override
    public void stop() throws InterruptedException {
        workerGroup.shutdownGracefully();
    }

    @Override
    public void reset() throws InterruptedException {
        stop();
        start();
    }

    @Override
    public void onChange() {

    }

}
