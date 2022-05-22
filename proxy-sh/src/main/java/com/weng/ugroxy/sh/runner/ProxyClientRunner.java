package com.weng.ugroxy.sh.runner;

import com.weng.ugroxy.proxyclient.NettyProxyClientContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


/**
 * @Author 翁丞健
 * @Date 2022/5/22 16:32
 * @Version 1.0.0
 */
@Component
@RequiredArgsConstructor
public class ProxyClientRunner implements ApplicationRunner {

    private final NettyProxyClientContainer nettyProxyClientContainer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        nettyProxyClientContainer.start();
    }
}
