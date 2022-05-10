package com.weng.ugroxy.proxycommon.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 21:50
 * @Version 1.0.0
 */
@AllArgsConstructor
@Getter
public enum CompressEnum   {
    /**
     * 不压缩
     */
    NONE((byte) 0x0, "noneZip"),
    /**
     * gzip压缩
     */
    GZIP((byte) 0x01, "gzip"),
    /**
     * deflate压缩
     */
    DEFLATE((byte) 0x02, "deflate");

    private final byte code;
    
    private final String name;

    public static String getName(byte code) {
        for (CompressEnum c : CompressEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

    public static CompressEnum getEnumByName(String name) {
        for (CompressEnum c : CompressEnum.values()) {
            if (Objects.equals(c.getName(), name)) {
                return c;
            }
        }
        return null;
    }
}
