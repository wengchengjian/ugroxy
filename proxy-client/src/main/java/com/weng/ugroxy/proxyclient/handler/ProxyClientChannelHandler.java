package com.weng.ugroxy.proxyclient.handler;

import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.support.handler.ServiceHandler;
import com.weng.ugroxy.proxycommon.utils.ApplicationContextUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author 翁丞健
 * @Date 2022/5/11 23:08
 * @Version 1.0.0
 */
@Slf4j
@Service
public class ProxyClientChannelHandler extends SimpleChannelInboundHandler<DefaultProxyMessage> {
    private List<ServiceHandler> serviceHandlers;

    @PostConstruct
    public void init(){
        ApplicationContext applicationContext = ApplicationContextUtil.getContext();

        // 获取所有处理器
        Map<String, ServiceHandler> serviceHandlerMap = applicationContext.getBeansOfType(ServiceHandler.class);

        this.serviceHandlers = new ArrayList<>(serviceHandlerMap.values());
        // 对处理器进行排序
        AnnotationAwareOrderComparator.sort(serviceHandlers);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultProxyMessage msg) throws Exception {
        doDispatch(ctx, msg);
    }

    public void doDispatch(ChannelHandlerContext ctx, DefaultProxyMessage msg){

        byte type = msg.getType();

        ServiceHandler serviceHandler = getHandler(type);

        serviceHandler.handle(ctx,msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.info("处理器执行失败:{}",cause.getMessage());
    }

    private ServiceHandler getHandler(byte type) {
        if(serviceHandlers!=null){
            for(ServiceHandler serviceHandler : serviceHandlers){
                if(serviceHandler.support(type)){
                    return serviceHandler;
                }
            }
        }
        throw  new RuntimeException("没有找到对应的处理器");
    }
}
