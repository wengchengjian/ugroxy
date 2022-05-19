package com.weng.ugroxy.proxyserver.handler;

import com.weng.ugroxy.proxycommon.constants.AttributeKeyEnum;
import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyRequestMessage;
import com.weng.ugroxy.proxycommon.utils.SequenceGenerator;
import com.weng.ugroxy.proxyserver.ProxyChannelManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.stereotype.Component;

import javax.management.Attribute;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author 翁丞健
 * @Date 2022/5/10 22:58
 * @Version 1.0.0
 */
@Component
@Slf4j
public class UserChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static AtomicLong userIdProducer = new AtomicLong(0);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
// 通知代理客户端
        Channel userChannel = ctx.channel();
        Channel proxyChannel = userChannel.attr(AttributeKeyEnum.NEXT_CHANNEL).get();
        if (proxyChannel == null) {
            // 该端口还没有代理客户端
            ctx.channel().close();
        } else {
            byte[] bytes = new byte[msg.readableBytes()];
            msg.readBytes(bytes);
            String userId = ProxyChannelManager.getUserChannelToken(userChannel);
            DefaultProxyMessage proxyMessage = new DefaultProxyMessage();
            proxyMessage.setType(RequestType.CLIENT_PROXY_TRANSFER_REQUEST.getCode());
            proxyMessage.setUri(userId);
            proxyMessage.setData(bytes);
            proxyChannel.writeAndFlush(proxyMessage);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        // 通知代理客户端
        Channel userChannel = ctx.channel();
        InetSocketAddress sa = (InetSocketAddress) userChannel.localAddress();
        Channel cmdChannel = ProxyChannelManager.getCmdChannel(sa.getPort());
        if (cmdChannel == null) {

            // 该端口还没有代理客户端
            ctx.channel().close();
        } else {

            // 用户连接断开，从控制连接中移除
            String userId = ProxyChannelManager.getUserChannelToken(userChannel);
            ProxyChannelManager.removeUserChannelFromCmdChannel(cmdChannel, userId);
            Channel proxyChannel = userChannel.attr(AttributeKeyEnum.NEXT_CHANNEL).get();
            if (proxyChannel != null && proxyChannel.isActive()) {
                proxyChannel.attr(AttributeKeyEnum.NEXT_CHANNEL).remove();
                proxyChannel.attr(AttributeKeyEnum.CLIENT_KEY).remove();
                proxyChannel.attr(AttributeKeyEnum.TOKEN).remove();

                proxyChannel.config().setOption(ChannelOption.AUTO_READ, true);
                // 通知客户端，用户连接已经断开
                DefaultProxyRequestMessage request = DefaultProxyRequestMessage.builder().seqId(SequenceGenerator.next()).uri(userId).build();
                DefaultProxyMessage message = DefaultProxyMessage.getDefaultMessage(request, RequestType.CLIENT_DISCONNECT_REQUEST.getCode());
                proxyChannel.writeAndFlush(message);
            }
        }

        super.channelInactive(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel userChannel = ctx.channel();
        InetSocketAddress sa = (InetSocketAddress) userChannel.localAddress();
        Channel cmdChannel = ProxyChannelManager.getCmdChannel(sa.getPort());

        if (cmdChannel == null) {

            // 该端口还没有代理客户端
            ctx.channel().close();
        } else {
            String userId = newUserId();
            String lanInfo = ProxyConfig.getInstance().getLanInfo(sa.getPort());
            // 用户连接到代理服务器时，设置用户连接不可读，等待代理后端服务器连接成功后再改变为可读状态
            userChannel.config().setOption(ChannelOption.AUTO_READ, false);
            ProxyChannelManager.addUserChannelToCmdChannel(cmdChannel, userId, userChannel);
            ProxyMessage proxyMessage = new ProxyMessage();
            proxyMessage.setType(ProxyMessage.TYPE_CONNECT);
            proxyMessage.setUri(userId);
            proxyMessage.setData(lanInfo.getBytes());
            cmdChannel.writeAndFlush(proxyMessage);
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

        // 通知代理客户端
        Channel userChannel = ctx.channel();
        InetSocketAddress sa = (InetSocketAddress) userChannel.localAddress();
        Channel cmdChannel = ProxyChannelManager.getCmdChannel(sa.getPort());
        if (cmdChannel == null) {

            // 该端口还没有代理客户端
            ctx.channel().close();
        } else {
            Channel proxyChannel = userChannel.attr(AttributeKeyEnum.NEXT_CHANNEL).get();
            if (proxyChannel != null) {
                proxyChannel.config().setOption(ChannelOption.AUTO_READ, userChannel.isWritable());
            }
        }

        super.channelWritabilityChanged(ctx);
    }

    private static String newUserId() {
        return String.valueOf(userIdProducer.incrementAndGet());
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        log.error("异常信息：", cause);
        ctx.close();
    }
}
