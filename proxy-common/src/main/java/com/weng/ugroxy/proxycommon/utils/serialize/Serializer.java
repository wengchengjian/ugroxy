package com.weng.ugroxy.proxycommon.utils.serialize;

import com.weng.ugroxy.proxycommon.constants.CodecEnum;
import com.weng.ugroxy.proxycommon.exception.SerializeException;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 21:53
 * @Version 1.0.0
 */
public interface Serializer {
    /**
     * 序列化
     * @param data 需要序列化的数据
     * @return 序列化后的字节数组
     */
    byte[] serialize(Object data) throws SerializeException;

    /**
     * 反序列化
     * @param data 需要反序列化的字节数组
     * @param clazz 反序列化的类型
     * @param <T> 反序列化的泛型
     * @return 反序列化后的对象
     */
    <T> T deserialize(byte[] data, Class<T> clazz) throws SerializeException;

    CodecEnum getSerializerType();
}
