package com.weng.ugroxy.proxycommon.autoconfigure;

import com.weng.ugroxy.proxycommon.annotation.LogAspect;
import com.weng.ugroxy.proxycommon.utils.compress.Compress;
import com.weng.ugroxy.proxycommon.utils.compress.impl.GzipCompress;
import com.weng.ugroxy.proxycommon.utils.serialize.Serializer;
import com.weng.ugroxy.proxycommon.utils.serialize.impl.KryoSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 22:15
 * @Version 1.0.0
 */
@Configuration
public class ProxyCommonAutoConfiguration {

    @Bean
    public ProxyCommonProperties proxyCommonProperties() {
        return new ProxyCommonProperties();
    }

    /**
     * 默认注入gzip压缩器
     */
    @Bean
    @ConditionalOnMissingBean
    public Compress compress() {
        return new GzipCompress();
    }

    /**
     * 默认注入kryo序列化器
     */
    @Bean
    @ConditionalOnMissingBean
    public Serializer serializer() {
        return new KryoSerializer();
    }

    /**
     * 在配置文件中配置开启日志切面
     */
    @Bean
    @ConditionalOnProperty(prefix = "ugroxy.logging", name = "enabled", havingValue = "true")
    public LogAspect logAspect(){
        return new LogAspect();
    }
}
