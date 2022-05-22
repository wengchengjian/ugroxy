package com.weng.ugroxy.proxyclient.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author 翁丞健
 * @Date 2022/5/22 16:58
 * @Version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "ugroxy.client")
public class UgroxyClientProperties {
    /**
     * 服务端地址
     */
    private String host = "localhost";
    /**
     * 服务端端口
     */
    private Integer port = 4396;

    /**
     * 用于认证的clientKey，默认是匿名
     */
    private String clientKey = "anonymous";

    /**
     * 重试策略
     */
    private Retry retry = new Retry();

    private List<Proxy> proxyList = new ArrayList<>();

    @Data
    public static class Retry {

        /**
         * 默认重试3次
         */
        private Integer times = 3;

        /**
         * 默认连接失败等待时间1秒
         */
        private Integer waitTime = 1000;

        /**
         * 默认最大等待时间为10秒
         */
        private Integer maxTime = 10000;


    }

    @Data
    public static class Proxy{
        /**
         * 需要代理的地址
         */
        private String host="localhost";

        /**
         * 需要代理的端口
         */
        private Integer port;

        /**
         * 需要代理的clientKey，默认为UUID
         */
        private String clientKey = UUID.randomUUID().toString();
    }

}
