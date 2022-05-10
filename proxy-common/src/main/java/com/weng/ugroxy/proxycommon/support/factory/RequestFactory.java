package com.weng.ugroxy.proxycommon.support.factory;

import com.weng.ugroxy.proxycommon.support.handler.ServiceHandler;
import com.weng.ugroxy.proxycommon.utils.ApplicationContextUtil;
import io.netty.channel.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 翁丞健
 * @Date 2022/4/29 23:06
 * @Version 1.0.0
 */
@Slf4j
@Component
public class RequestFactory {

    private  final Map<Byte,Class<?>> requestHolderMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        ApplicationContext context = ApplicationContextUtil.getContext();

        Map<String, ServiceHandler> beansOfType = context.getBeansOfType(ServiceHandler.class);

    }

    public Class<?> getRequestClass(Byte type){
        Class<?> requestClazz = requestHolderMap.get(type);

        if(requestClazz == null){
            log.error("requestClazz is null");
            throw new RuntimeException("requestClazz is null");
        }
        return requestClazz;
    }
}
