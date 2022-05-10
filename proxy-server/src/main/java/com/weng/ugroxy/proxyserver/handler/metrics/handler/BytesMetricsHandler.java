package com.weng.ugroxy.proxyserver.handler.metrics.handler;

import com.weng.ugroxy.proxyserver.handler.metrics.MetricsCollectorFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Address;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author 翁丞健
 * @Date 2022/5/10 22:45
 * @Version 1.0.0
 */
@Component
@Slf4j
public class BytesMetricsHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        InetSocketAddress address = (InetSocketAddress)ctx.channel().localAddress();

        int port = address.getPort();

        MetricsCollectorFactory.MetricsCollector metricsCollector = MetricsCollectorFactory.getMetricsCollector(port);

        AtomicLong readBytes = metricsCollector.getReadBytes();

        metricsCollector.incrementReadBytes(buf.readableBytes());
        metricsCollector.incrementReadMsgs(1);
        log.info("{}--- read {} byte", address.getHostName(), readBytes.get());

        ctx.fireChannelRead(msg);
    }



    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        InetSocketAddress address = (InetSocketAddress)ctx.channel().localAddress();

        int port = address.getPort();

        MetricsCollectorFactory.MetricsCollector metricsCollector = MetricsCollectorFactory.getMetricsCollector(port);

        AtomicLong writeBytes = metricsCollector.getWriteBytes();

        metricsCollector.incrementReadBytes(buf.readableBytes());
        metricsCollector.incrementReadMsgs(1);
        log.info("{}--- write {} byte", address.getHostName(), writeBytes.get());

        super.write(ctx, msg, promise);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress)ctx.channel().localAddress();

        int port = address.getPort();

        MetricsCollectorFactory.MetricsCollector metricsCollector = MetricsCollectorFactory.getMetricsCollector(port);

        metricsCollector.getChannels().incrementAndGet();

        log.info("{}--- connect server port {}", address.getHostName(), port);


        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress)ctx.channel().localAddress();

        int port = address.getPort();

        MetricsCollectorFactory.MetricsCollector metricsCollector = MetricsCollectorFactory.getMetricsCollector(port);

        metricsCollector.getChannels().decrementAndGet();

        log.info("{}--- disconnect server port {}", address.getHostName(),port);

        super.channelInactive(ctx);
    }
}
