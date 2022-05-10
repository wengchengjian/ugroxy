package com.weng.ugroxy.proxycommon.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Author 翁丞健
 * @Date 2022/4/28 22:34
 * @Version 1.0.0
 */
public class MessageConstant {
    /**
     * 长度为6的魔数，用来标识自定义协议的类型
     */
    public static final byte[] MAGIC_NUMBER = new byte[]{ (byte)'U', (byte)'G', (byte)'R', (byte)'O',(byte)'X',(byte)'Y' };
    /**
     * 补齐空位
     */
    public static final byte[] FILL_NUMBER = new byte[]{ (byte)'S' , (byte)'W' };

    /**
     * 默认UTF-8编码
     */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * 版本号
     */
    public static final byte VERSION = 1;

    /**
     * 消息头长度
     */
    public static final byte HEAD_LENGTH = 16;


    public static final String PING = "ping";

    public static final String PONG = "pong";

}
