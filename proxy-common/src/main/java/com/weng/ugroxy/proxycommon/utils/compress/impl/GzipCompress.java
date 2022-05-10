package com.weng.ugroxy.proxycommon.utils.compress.impl;

import com.weng.ugroxy.proxycommon.constants.CompressEnum;
import com.weng.ugroxy.proxycommon.exception.CompressException;
import com.weng.ugroxy.proxycommon.utils.compress.Compress;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 21:59
 * @Version 1.0.0
 */
@Component
public class GzipCompress implements Compress {

    private static final int BUFFER_SIZE = 1024 * 4;


    @Override
    public byte[] compress(byte[] bytes) {
        if(bytes==null && bytes.length==0){
            throw new IllegalArgumentException("需要压缩的字节数组不能为空");
        }

        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(out)){

            gzip.write(bytes);
            gzip.flush();
            gzip.finish();
            return out.toByteArray();
        } catch (IOException e) {
            throw new CompressException("gizp压缩失败: {}",e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        if(bytes==null && bytes.length==0){
            throw new IllegalArgumentException("需要解压的字节数组不能为空");
        }

        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPInputStream gunzip = new GZIPInputStream(new ByteArrayInputStream(bytes))){
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;

            while ((n = gunzip.read(buffer)) > -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new CompressException("gizp解压失败: {}",e);
        }
    }

    @Override
    public CompressEnum getCompressType() {
        return CompressEnum.GZIP;
    }
}
