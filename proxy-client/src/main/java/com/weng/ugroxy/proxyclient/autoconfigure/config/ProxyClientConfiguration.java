package com.weng.ugroxy.proxyclient.autoconfigure.config;

import com.weng.ugroxy.proxyclient.NettyProxyClientContainer;
import com.weng.ugroxy.proxyclient.autoconfigure.properties.UgroxyClientProperties;
import com.weng.ugroxy.proxycommon.container.Container;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Author 翁丞健
 * @Date 2022/5/13 22:17
 * @Version 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(UgroxyClientProperties.class)
public class ProxyClientConfiguration {
    @Bean("userBootstrap")
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

    @Bean
    public Container container(){
        return new NettyProxyClientContainer();
    }

    @PostConstruct
    public void log(){
        log.info("Ugroxy Client is autoconfiguring.");
    }


}
