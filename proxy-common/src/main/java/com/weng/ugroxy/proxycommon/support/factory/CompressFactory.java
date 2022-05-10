package com.weng.ugroxy.proxycommon.support.factory;

import com.weng.ugroxy.proxycommon.constants.CompressEnum;
import com.weng.ugroxy.proxycommon.constants.CompressEnum;
import com.weng.ugroxy.proxycommon.utils.ApplicationContextUtil;
import com.weng.ugroxy.proxycommon.utils.compress.Compress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 压缩器工厂
 * @Author 翁丞健
 * @Date 2022/4/28 23:09
 * @Version 1.0.0
 */
@Component
@Slf4j
public class CompressFactory {

    private final ConcurrentHashMap<Byte, Compress> compressMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        ApplicationContext context = ApplicationContextUtil.getContext();

        Map<String, Compress> allCompresses = context.getBeansOfType(Compress.class);

        for(Compress compress : allCompresses.values()){
            CompressEnum compressType = compress.getCompressType();
            if(compressType == null){
                log.error("错误的压缩类型");
                throw new RuntimeException("错误的压缩类型");
            }
            byte key = (byte) compressType.getCode();
            if(compressMap.containsKey(key)){
                log.error("存在多个压缩器实现了{}压缩器",compressType.getName());
                throw new IllegalArgumentException("存在多个压缩器实现了同一种类型的压缩器");
            }
            compressMap.put(key,compress);
        }
    }

    public final Compress getInstance(String name){
        return compressMap.get(Objects.requireNonNull(CompressEnum.getEnumByName(name)).getCode());
    }

    public final Compress getInstance(CompressEnum codec){
        return compressMap.get(codec.getCode());
    }

    public final Compress getInstance(byte code){
        return compressMap.get(code);
    }
}
