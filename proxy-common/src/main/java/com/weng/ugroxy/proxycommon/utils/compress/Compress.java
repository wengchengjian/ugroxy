package com.weng.ugroxy.proxycommon.utils.compress;

import com.weng.ugroxy.proxycommon.constants.CompressEnum;
import com.weng.ugroxy.proxycommon.exception.CompressException;
/**
 * @Author 翁丞健
 * @Date 2022/4/26 21:53
 * @Version 1.0.0
 */
public interface Compress {

    /**
     * 压缩字节数组
     * @param data
     * @return
     */
    byte[] compress(byte[] data) throws CompressException;

    /**
     * 解压缩字节数组
     * @param data
     * @return
     */
    byte[] decompress(byte[] data) throws CompressException;

    CompressEnum getCompressType();
}
