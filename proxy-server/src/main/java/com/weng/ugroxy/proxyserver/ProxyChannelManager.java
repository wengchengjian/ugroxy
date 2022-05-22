package com.weng.ugroxy.proxyserver;

import com.weng.ugroxy.proxycommon.constants.AttributeKeyEnum;
import com.weng.ugroxy.proxyserver.config.ServerProxyConfig;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
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

    private static final Map<String,String> DNS_TO_CLIENTKEY = new ConcurrentHashMap<>();


    public static void addDnsToClientKey(String dns,String clientKey){
        DNS_TO_CLIENTKEY.put(dns,clientKey);
    }

    public static String getClientKeyByDns(String dns){
        return DNS_TO_CLIENTKEY.get(dns);
    }

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
    public static void addUserChannelToCmdChannel(Channel cmdChannel, String userId, Channel userChannel) {
        InetSocketAddress sa = (InetSocketAddress) userChannel.localAddress();
        String netInfo = ServerProxyConfig.getInstance().getNetInfo(sa.getPort());
        userChannel.attr(AttributeKeyEnum.TOKEN).set(userId);
        userChannel.attr(REQUEST_NET_INFO).set(netInfo);
        cmdChannel.attr(USER_CHANNELS).get().put(userId, userChannel);
    }

    public static Channel getCmdChannel(String clientKey) {
        return cmdChannelMapping.get(clientKey);
    }

    public static void addCmdChannel(String clientKey, Channel channel) {
//        if(CollectionUtils.isEmpty(ports)){
//            log.error("add cmd channel failed, ports is empty");
//            throw new IllegalArgumentException("port can not be null");
//        }
//
//        for(int port : ports){
//            portCmdChannelMapping.put(port,channel);
//        }
//
//        channel.attr(CHANNEL_PORTS).set(ports);
        channel.attr(CHANNEL_CLIENT_KEY).set(clientKey);
        channel.attr(USER_CHANNELS).set(new ConcurrentHashMap<String,Channel>());
        cmdChannelMapping.put(clientKey,channel);
    }

    public static Channel getUserChannel(Channel cmdChannel, String token) {
        return  cmdChannel.attr(USER_CHANNELS).get().get(token);
    }
    public static String getUserChannelToken(Channel userChannel) {
        return userChannel.attr(AttributeKeyEnum.TOKEN).get();
    }

    public static Channel removeUserChannelFromCmdChannel(Channel channel, String userId) {
        Map<String, Channel> userChannelMap = channel.attr(USER_CHANNELS).get();
        if(userChannelMap==null){
            return null;
        }
        return userChannelMap.remove(userId);
    }

    /**
     * 获取用户请求的内网IP端口信息
     *
     * @param userChannel
     * @return
     */
    public static String getUserChannelRequestLanInfo(Channel userChannel) {
        return userChannel.attr(REQUEST_NET_INFO).get();
    }

    /**
     * 获取代理控制客户端连接绑定的所有用户连接
     *
     * @param cmdChannel
     * @return
     */
    public static Map<String, Channel> getUserChannels(Channel cmdChannel) {
        return cmdChannel.attr(USER_CHANNELS).get();
    }
}
