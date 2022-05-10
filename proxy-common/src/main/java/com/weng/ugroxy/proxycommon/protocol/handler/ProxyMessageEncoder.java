package com.weng.ugroxy.proxycommon.protocol.handler;

import com.weng.ugroxy.proxycommon.constants.MessageConstant;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.support.factory.CompressFactory;
import com.weng.ugroxy.proxycommon.support.factory.SerializerFactory;
import com.weng.ugroxy.proxycommon.utils.compress.Compress;
import com.weng.ugroxy.proxycommon.utils.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.weng.ugroxy.proxycommon.constants.RequestType.*;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 21:40
 * @Version 1.0.0
 */
@Component
@Slf4j
public class ProxyMessageEncoder extends MessageToByteEncoder<DefaultProxyMessage> {

    @Autowired
    private CompressFactory compressFactory;

    @Autowired
    private SerializerFactory serializerFactory;

    @Override
    protected void encode(ChannelHandlerContext ctx, DefaultProxyMessage msg, ByteBuf out) throws Exception {
        // 写入消息头魔数标识协议类型, 占6个字节
        out.writeBytes(MessageConstant.MAGIC_NUMBER);
        // 写入消息头版本号,占一个字节
        out.writeByte(MessageConstant.VERSION);
        // 写入长度域,占四个字节
        out.writerIndex(out.writerIndex()+4);

        byte codec = msg.getCodec();
        // 写入序列化类型,占一个字节
        out.writeByte(codec);
        byte compressType = msg.getCompress();
        // 写入压缩类型,占1个字节
        out.writeByte(compressType);
        // 写入消息类型,占1个字节
        out.writeByte(msg.getType());
        // 写入填充数据
        out.writeBytes(MessageConstant.FILL_NUMBER);
        // 写入消息体
        int fullLength = MessageConstant.HEAD_LENGTH;
        // 只要不是心跳包，就需要写入消息体
        byte[] body = new byte[0];
        if(msg.getType()!=HEART_BEAT_REQUEST.getCode() && msg.getType()!=HEART_BEAT_RESPONSE.getCode()){

            Object data = msg.getData();
            // 获取指定的序列化工具
            Serializer serializer = serializerFactory.getInstance(codec);
            // 序列化消息体
            body = serializer.serialize(data);
            // 获取指定的压缩工具
            Compress compressUtil = compressFactory.getInstance(compressType);
            // 压缩消息体
            body = compressUtil.compress(body);
            // 最终写入消息体长度
            fullLength = fullLength + body.length;
        }
        if(!ArrayUtils.isEmpty(body)){
            out.writeBytes(body);
        }
        // 记录最终消息长度
        int writeIndex = out.writerIndex();
        // 跳过魔数和版本号
        out.writerIndex(MessageConstant.MAGIC_NUMBER.length + 1);
        // 写入整个请求消息长度
        out.writeInt(fullLength);
        // 还原消息写入索引
        out.writerIndex(writeIndex);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("ProxyMessageEncoder encode failed:{}", cause.getMessage());
    }
}
