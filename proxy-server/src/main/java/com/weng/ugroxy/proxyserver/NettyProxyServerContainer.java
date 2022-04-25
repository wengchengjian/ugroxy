package com.weng.ugroxy.proxyserver;

import com.weng.ugroxy.proxycommon.container.Container;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;

/**
 * @Author 翁丞健
 * @Date 2022/4/25 23:03
 * @Version 1.0.0
 */
@Slf4j
public class NettyProxyServerContainer implements Container, Serializable {

    private static final long serialVersionUID = -8586927507516656849L;

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workGroup;

    public NettyProxyServerContainer(){
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();
    }

    @Override
    public void start() {

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
