package com.weng.ugroxy.proxyserver;

import com.weng.ugroxy.proxycommon.constants.AttributeKeyEnum;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 翁丞健
 * @Date 2022/5/1 17:11
 * @Version 1.0.0
 */
@Slf4j
public class ProxyChannelManager {

    private static final AttributeKey<Map<String, Channel>> USER_CHANNELS = AttributeKey.newInstance("user_channels");

    private static final AttributeKey<String> REQUEST_NET_INFO = AttributeKey.newInstance("request_net_info");

    private static final AttributeKey<List<Integer>> CHANNEL_PORTS  = AttributeKey.newInstance("channel_ports");

    private static final AttributeKey<String> CHANNEL_CLIENT_KEY = AttributeKey.newInstance("channel_client_key");

    private static final Map<Integer,Channel> portCmdChannelMapping = new ConcurrentHashMap<>();

    private static final Map<String,Channel> cmdChannelMapping = new ConcurrentHashMap<>();


    public static void removeCmdChannel(Channel channel){
        log.info("user client closed channel, will remove user cmd channel");
        List<Integer> ports = channel.attr(CHANNEL_PORTS).get();

        if(CollectionUtils.isEmpty(ports)){
            log.warn("user client closed channel, but channel ports is empty");
            return;
        }

        String clientKey = channel.attr(CHANNEL_CLIENT_KEY).get();

        if(clientKey == null){
    }
}

    public static Channel getCmdChannel(String clientKey) {
        return cmdChannelMapping.get(clientKey);
    }

    public static void addCmdChannel(List<Integer> ports, String clientKey, Channel channel) {
        if(CollectionUtils.isEmpty(ports)){
            log.error("add cmd channel failed, ports is empty");
            throw new IllegalArgumentException("port can not be null");
        }

        for(int port : ports){
            portCmdChannelMapping.put(port,channel);
        }

        channel.attr(CHANNEL_PORTS).set(ports);
        channel.attr(CHANNEL_CLIENT_KEY).set(clientKey);
        channel.attr(USER_CHANNELS).set(new ConcurrentHashMap<String,Channel>());
        cmdChannelMapping.put(clientKey,channel);
    }

    public static Channel getUserChannel(Channel cmdChannel, String token) {
        return  cmdChannel.attr(USER_CHANNELS).get().get(token);
    }

    public static Channel removeUserChannelFromCmdChannel(Channel channel, String userId) {
        Map<String, Channel> userChannelMap = channel.attr(USER_CHANNELS).get();
        if(userChannelMap==null){
            return null;
        }
        return userChannelMap.remove(userId);
    }
}
