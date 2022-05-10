package com.weng.ugroxy.proxycommon.constants;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 该枚举类用于描述编码类型，code和name都是固定且唯一的，不可修改
 * @Author 翁丞健
 * @Date 2022/4/28 23:05
 * @Version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum CodecEnum {
    /**
     * Kryo序列化方式
     */
    KYRO((byte) 0x01, "kyro"),

    /**
     * TODO 待实现
     * Protostuff序列化方式
     */
    PROTOSTUFF((byte) 0x02, "protostuff");;

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CodecEnum c : CodecEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

    public static CodecEnum getEnumByName(String name) {
        for (CodecEnum c : CodecEnum.values()) {
            if (Objects.equals(c.getName(), name)) {
                return c;
            }
        }
        return null;
    }
}
