package com.weng.ugroxy.proxycommon.constants;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * @Author 翁丞健
 * @Date 2022/5/1 18:24
 * @Version 1.0.0
 */
public class AttributeKeyEnum {
    public  static final AttributeKey<Channel> NEXT_CHANNEL = AttributeKey.newInstance("next_channel");

    public static final AttributeKey<String> TOKEN = AttributeKey.newInstance("token");

    public static final AttributeKey<String> CLIENT_KEY = AttributeKey.newInstance("client_key");
}
