package com.weng.ugroxy.proxycommon.support;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;

import java.time.Clock;

/**
 * @Author 翁丞健
 * @Date 2022/5/1 13:47
 * @Version 1.0.0
 */

public class ProxyConfigApplicationEvent extends ApplicationEvent {

    public ProxyConfigApplicationEvent(Object source) {
        super(source);
    }

    public ProxyConfigApplicationEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
