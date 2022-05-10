package com.weng.ugroxy.proxycommon.support.factory;

import com.weng.ugroxy.proxycommon.constants.CodecEnum;
import com.weng.ugroxy.proxycommon.utils.ApplicationContextUtil;
import com.weng.ugroxy.proxycommon.utils.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列化器工程
 * @Author 翁丞健
 * @Date 2022/4/28 23:08
 * @Version 1.0.0
 */
@Component
@Slf4j
public class SerializerFactory  {

    private ConcurrentHashMap<Byte,Serializer> serializerMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        ApplicationContext context = ApplicationContextUtil.getContext();

        Map<String, Serializer> allSerializers = context.getBeansOfType(Serializer.class);

        for(Serializer serializer : allSerializers.values()){
            CodecEnum serializerType = serializer.getSerializerType();
            if(serializerType == null){
                log.error("错误的序列化类型");
                throw new RuntimeException("错误的序列化类型");
            }
            byte key = (byte) serializerType.getCode();
            if(serializerMap.containsKey(key)){
                log.error("存在多个序列化器实现了{}序列化器",serializerType.getName());
                throw new IllegalArgumentException("存在多个序列化器实现了同一种类型的序列化器");
            }
            serializerMap.put(key,serializer);
        }
    }

    public final Serializer getInstance(String name){
        return serializerMap.get(Objects.requireNonNull(CodecEnum.getEnumByName(name)).getCode());
    }

    public final Serializer getInstance(CodecEnum codec){
        return serializerMap.get(codec.getCode());
    }

    public final Serializer getInstance(byte code){
        return serializerMap.get(code);
    }
}
