package com.weng.ugroxy.proxycommon.autoconfigure;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 22:16
 * @Version 1.0.0
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "ugroxy")
public class ProxyCommonProperties {
    public String compress;

    public String codec;

    private LogState logging;

    private class LogState {
        private boolean enabled;
    }
}
