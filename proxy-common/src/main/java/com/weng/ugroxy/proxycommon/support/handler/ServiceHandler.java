package com.weng.ugroxy.proxycommon.support.handler;

import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.constants.StatusCode;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyResponseMessage;
import com.weng.ugroxy.proxycommon.utils.Result;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.weng.ugroxy.proxycommon.constants.StatusCode.Failure;
import static com.weng.ugroxy.proxycommon.constants.StatusCode.SUCCESS;

/**
 * @Author 翁丞健
 * @Date 2022/4/29 23:07
 * @Version 1.0.0
 */
public interface ServiceHandler<T> {
    Logger log = LoggerFactory.getLogger(ServiceHandler.class);

    RequestType[] getSupportTypes();

    default boolean support(byte type){
        RequestType[] supportTypes = getSupportTypes();
        for (RequestType supportType : supportTypes) {
            if(type == supportType.getCode()){
                return true;
            }
        }
        return false;
    }

    default boolean support(RequestType type){
        RequestType[] supportTypes = getSupportTypes();
        for (RequestType supportType : supportTypes) {
            if(type == supportType){
                return true;
            }
        }
        return false;
    }
    default void handle(ChannelHandlerContext ctx, T proxyMessage){
            try{
                doService(ctx,  proxyMessage);
            }catch (Exception e){
                log.error("处理异常:{}", e);
                exceptinCaguht(ctx, e);
            }
    }

    void doService(ChannelHandlerContext ctx, T proxyMessage);

    default void exceptinCaguht(ChannelHandlerContext ctx, Throwable cause){
        log.error("关闭连接:{}", cause);
        ctx.close();
    }

    default void success(ChannelHandlerContext ctx, Object returnMessage){
        success(ctx,SUCCESS, returnMessage);
    }

    default void success(ChannelHandlerContext ctx, StatusCode statusCode,Object returnMessage){
        ctx.writeAndFlush(getResponse(statusCode,returnMessage));
    }

    default void success(ChannelHandlerContext ctx, StatusCode statusCode){
        success(ctx,statusCode,null);
    }

    default DefaultProxyMessage getResponse(StatusCode statusCode,Object ret){
        return DefaultProxyMessage.getDefaultMessage(Result.of(statusCode,ret));

    }

    default void failure(ChannelHandlerContext ctx, String returnMessage){
        failure(ctx,Failure,returnMessage);
    }

    default void failure(ChannelHandlerContext ctx, StatusCode statusCode, String returnMessage){
        ctx.writeAndFlush(getResponse(statusCode,returnMessage));
    }

    default void failure(ChannelHandlerContext ctx, StatusCode statusCode){
        failure(ctx, statusCode, null);
    }


}
