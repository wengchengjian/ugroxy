package com.weng.ugroxy.proxyserver.runner;

import com.weng.ugroxy.proxycommon.container.Container;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Author 翁丞健
 * @Date 2022/5/5 22:06
 * @Version 1.0.0
 */
@Component
public class ContainerRunner implements ApplicationRunner {
    @Autowired
    private Container container;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        container.start();
    }
}
