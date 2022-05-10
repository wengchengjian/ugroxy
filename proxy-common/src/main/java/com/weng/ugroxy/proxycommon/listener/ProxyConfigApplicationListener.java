package com.weng.ugroxy.proxycommon.listener;

import com.weng.ugroxy.proxycommon.support.ProxyConfigApplicationEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @Author 翁丞健
 * @Date 2022/5/1 14:35
 * @Version 1.0.0
 */
@Component
public class ProxyConfigApplicationListener implements SmartApplicationListener {
    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == ProxyConfigApplicationEvent.class;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return SmartApplicationListener.super.supportsSourceType(sourceType);
    }

    @Override
    public int getOrder() {
        return SmartApplicationListener.super.getOrder();
    }

    @Override
    public String getListenerId() {
        return SmartApplicationListener.super.getListenerId();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        ProxyConfigApplicationEvent configEvent = (ProxyConfigApplicationEvent) event;

        String jsonConfig = (String) configEvent.getSource();

    }
}
