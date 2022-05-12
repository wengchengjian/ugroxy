package com.weng.ugroxy.proxyclient;

import com.weng.ugroxy.proxyclient.listener.NettyProxyChannelBorrowListener;
import com.weng.ugroxy.proxycommon.constants.AttributeKeyEnum;
import com.weng.ugroxy.proxycommon.support.Config;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author 翁丞健
 * @Date 2022/5/12 21:49
 * @Version 1.0.0
 */
@Slf4j

public class ClientChannelManager {

    private static final AttributeKey<Boolean> USER_CHANNEL_WRITEABLE = AttributeKey.newInstance("user_channel_writeable");

    private static final AttributeKey<Boolean> CLIENT_CHANNEL_WRITEABLE = AttributeKey.newInstance("client_channel_writeable");

    private static final int MAX_POOL_SIZE = 100;

    private static final Map<String, Channel> REAL_SERVER_CHANNEL = new ConcurrentHashMap<>(64);

    private static final ConcurrentLinkedQueue<Channel> PROXY_CHANNEL_POOL = new ConcurrentLinkedQueue<>();

    private static volatile Channel CMD_CHANNEL;

    private static Config config = Config.getInstance();

    public static void borrowProxyChannel(Bootstrap bootstrap,final NettyProxyChannelBorrowListener listener) {
        Channel channel = PROXY_CHANNEL_POOL.poll();
        if (channel != null) {
            listener.success(channel);
            return;
        }

        bootstrap.connect(config.getStringProperty("server.host","127.0.0.1"), config.getIntProperty("server.port",7869)).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    listener.success(future.channel());
                } else {
                    log.warn("connect proxy server failed", future.cause());
                    listener.error(future.cause());
                }
            }
        });
    }
    public static void returnProxyChannel(Channel channel){
        if(PROXY_CHANNEL_POOL.size() >= MAX_POOL_SIZE){
            channel.close();
            return;
        }
        channel.config().setOption(ChannelOption.AUTO_READ,true);

        channel.attr(AttributeKeyEnum.NEXT_CHANNEL).remove();

        PROXY_CHANNEL_POOL.offer(channel);

        log.debug("return proxy channel to the pool. channel is {},pool size is {}", channel, PROXY_CHANNEL_POOL.size());
    }

    public static boolean removeProxyChannel(Channel channel){
        return PROXY_CHANNEL_POOL.remove(channel);
    }

    public static void setCmdChannel(Channel channel) {
        CMD_CHANNEL = channel;
    }

    public static Channel getCmdChannel(){
        return CMD_CHANNEL;
    }

    public static void setRealServerChannelToken(Channel realServerChannel, String userId) {
        realServerChannel.attr(AttributeKeyEnum.TOKEN).set(userId);
    }

    public static String getRealServerChannelToken(Channel realServerChannel) {
        return realServerChannel.attr(AttributeKeyEnum.TOKEN).get();
    }

    public static Channel getRealServerChannel(String token) {
        return REAL_SERVER_CHANNEL.get(token);
    }

    public static void addRealServerChannel(String token, Channel realServerChannel) {
        REAL_SERVER_CHANNEL.put(token, realServerChannel);
    }

    public static Channel removeRealServerChannel(String token) {
        return REAL_SERVER_CHANNEL.remove(token);
    }

    public static boolean isRealServerReadable(Channel realServerChannel) {
        return realServerChannel.attr(CLIENT_CHANNEL_WRITEABLE).get() && realServerChannel.attr(USER_CHANNEL_WRITEABLE).get();
    }

    public static void clearRealServerChannels(){
        log.warn("channel closed,clear all real server channels");

        for(Channel channel : REAL_SERVER_CHANNEL.values()){
            if(channel.isActive()){
                channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }else{
                channel.close();
            }
        }
        // clear all real server channels memory
        REAL_SERVER_CHANNEL.clear();
    }

}
