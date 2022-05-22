package com.weng.ugroxy.proxyserver.autoconfigure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author 翁丞健
 * @Date 2022/5/5 21:54
 * @Version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "ugroxy.proxy")
public class UgroxyServerProperties {

    private String host = "localhost";

    private int port = 4396;

    private SSL ssl = new SSL();


    @Data
    public class SSL{
        private boolean enabled = false;

        private String host = "localhost";

        private String port = "7989";

    }
}
