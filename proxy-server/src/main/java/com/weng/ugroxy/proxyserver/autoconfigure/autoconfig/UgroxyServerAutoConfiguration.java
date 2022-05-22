package com.weng.ugroxy.proxyserver.autoconfigure.autoconfig;

import com.weng.ugroxy.proxycommon.container.Container;
import com.weng.ugroxy.proxyserver.NettyProxyServerContainer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author 翁丞健
 * @Date 2022/5/5 22:00
 * @Version 1.0.0
 */
@Configuration
public class UgroxyServerAutoConfiguration {

    @Bean
    public Container container() {
        return new NettyProxyServerContainer();
    }

    @Bean
    public NioEventLoopGroup bossGroup(){
        return new NioEventLoopGroup();
    }

    @Bean
    public NioEventLoopGroup workGroup(){
        return new NioEventLoopGroup();
    }

    @Bean("bootstrap")
    public ServerBootstrap bootstrap(){
        return new ServerBootstrap();
    }

    @Bean("sslBootstrap")
    public ServerBootstrap sslBootstrap(){
        return new ServerBootstrap();
    }

    @Bean("userBootstrap")
    public ServerBootstrap userBootstrap(){
        return new ServerBootstrap();
    }

}
