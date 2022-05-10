package com.weng.ugroxy.proxyserver.autoconfigure.autoconfig;

import com.weng.ugroxy.proxycommon.container.Container;
import com.weng.ugroxy.proxyserver.NettyProxyServerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author 翁丞健
 * @Date 2022/5/5 22:00
 * @Version 1.0.0
 */
@Configuration
public class UgroxyServerAutoConfiguration {

    @Bean
    public Container container() {
        return new NettyProxyServerContainer();
    }


}
