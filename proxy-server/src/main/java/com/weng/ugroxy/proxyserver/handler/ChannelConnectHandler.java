package com.weng.ugroxy.proxyserver.handler;

import com.weng.ugroxy.proxycommon.constants.AttributeKeyEnum;
import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyRequestMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyResponseMessage;
import com.weng.ugroxy.proxycommon.support.handler.ServiceHandler;
import com.weng.ugroxy.proxyserver.ProxyChannelManager;
import com.weng.ugroxy.proxyserver.utils.DnsRegisterUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;

import static com.weng.ugroxy.proxycommon.constants.RequestType.*;
/**
 * @Author 翁丞健
 * @Date 2022/5/1 17:54
 * @Version 1.0.0
 */
@Service
@Slf4j
public class ChannelConnectHandler implements ServiceHandler<DefaultProxyMessage<DefaultProxyRequestMessage>> {
    @Override
    public RequestType getSupportTypes() {
        return CLIENT_CONNECT_REQUEST;
    }

    @Override
    public RequestType getReturnType() {
        return CLIENT_CONNECT_RESPONSE;
    }

    @Override
    public void doService(ChannelHandlerContext ctx, DefaultProxyMessage<DefaultProxyRequestMessage> proxyMessage) {
        log.info("收到客户端连接请求");
        String clientKey = proxyMessage.getData().getClientKey();

        String[] serverInfo = new String(proxyMessage.getData().getBody()).split(":");

        if(StringUtils.isEmpty(clientKey)){
            log.error("客户端连接请求clientKey为空");
            failure(ctx, "客户端连接请求uri为空");
            return;
        }

        if(serverInfo.length != 2){
            failure(ctx, "客户端连接请求地址格式错误");
            log.error("客户端连接请求地址格式错误");
            return;
        }


        String host = serverInfo[0];

        Integer ip = Integer.parseInt(serverInfo[1]);

        // 生成动态域名
        String dns = DnsRegisterUtil.registerDns();

        ProxyChannelManager.addDnsToClientKey(dns,clientKey);

        // 返回dns域名
        success(ctx,dns);
    }
}
