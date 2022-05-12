package com.weng.ugroxy.proxyclient;

import com.weng.ugroxy.proxyclient.handler.ProxyClientChannelHandler;
import com.weng.ugroxy.proxyclient.handler.RealServerChannelHandler;
import com.weng.ugroxy.proxycommon.constants.CodecEnum;
import com.weng.ugroxy.proxycommon.constants.CompressEnum;
import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.container.Container;
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

    private static  Integer TIMES = 0;

    private static Integer waitTime = 0;

    private synchronized void connectProxyServer() {

        try {
            // 等待重试时间,时间逐渐递增
            waitRetryTime();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(TIMES < config.getIntProperty("client.retry.times",5)){
            TIMES++;
        }

        bootstrap.connect(config.getStringProperty("server.host","127.0.0.1"), config.getIntProperty("server.port",7869)).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // 连接成功，向服务器发送客户端认证信息（clientKey）
                    ClientChannelManager.setCmdChannel(future.channel());

                    authToServer(future.channel());

                    log.info("connect proxy server success, {}", future.channel());
                } else {
                    log.warn("connect proxy server failed", future.cause());
                    connectProxyServer();
                }
            }
        });
    }

    private void waitRetryTime() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(waitTime);
        incrementWaitTime();
    }

    private synchronized void incrementWaitTime() {
        int increment = config.getIntProperty("client.retry.wait.increment", 1000);

        int maxWaitTime = config.getIntProperty("client.retry.wait.maxTime", 60000);

        if(waitTime+increment < maxWaitTime){
            waitTime += increment;
        }else{
            waitTime = maxWaitTime;
        }
    }

    public void authToServer(Channel channel){
        DefaultProxyRequestMessage message = DefaultProxyRequestMessage.builder()
                .body(null).seqId(SequenceGenerator.next()).uri(config.getStringProperty("client.key","anonymous")).build();

        DefaultProxyMessage<DefaultProxyRequestMessage> proxyMessage = DefaultProxyMessage.getDefaultMessage(message,RequestType.CLIENT_AUTH_REQUEST.getCode());
        channel.writeAndFlush(proxyMessage);
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
