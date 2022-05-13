package com.weng.ugroxy.proxyclient.autoconfigure.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author 翁丞健
 * @Date 2022/5/13 22:17
 * @Version 1.0.0
 */
@Configuration
public class ProxyClientConfiguration {
    @Bean("bootstrap")
    public Bootstrap bootstrap1(){
        return new Bootstrap();
    }

    @Bean("proxyBootstrap")
    public Bootstrap bootstrap2(){
        return new Bootstrap();
    }

    @Bean("workerGroup")
    public NioEventLoopGroup workerGroup(){
        return new NioEventLoopGroup();
    }
}
