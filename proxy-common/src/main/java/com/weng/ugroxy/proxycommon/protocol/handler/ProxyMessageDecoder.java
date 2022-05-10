package com.weng.ugroxy.proxycommon.protocol.handler;

import com.weng.ugroxy.proxycommon.constants.MessageConstant;
import com.weng.ugroxy.proxycommon.constants.RequestType;
import com.weng.ugroxy.proxycommon.exception.NotSupportProtocolException;
import com.weng.ugroxy.proxycommon.exception.NotSupportedVersionException;
import com.weng.ugroxy.proxycommon.protocol.message.DefaultProxyMessage;
import com.weng.ugroxy.proxycommon.support.factory.CompressFactory;
import com.weng.ugroxy.proxycommon.support.factory.RequestFactory;
import com.weng.ugroxy.proxycommon.support.factory.SerializerFactory;
import com.weng.ugroxy.proxycommon.utils.compress.Compress;
import com.weng.ugroxy.proxycommon.utils.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.ByteOrder;

/**
 * @Author 翁丞健
 * @Date 2022/4/26 21:40
 * @Version 1.0.0
 */
@Slf4j
@Component
public class ProxyMessageDecoder extends LengthFieldBasedFrameDecoder {

    @Autowired
    private SerializerFactory serializerFactory;

    @Autowired
    private CompressFactory compressFactory;

    @Autowired
    private RequestFactory requestFactory;

    private static final int MAX_FRAME_SIZE  = 2*1024*1024;

    private static final int LEGNTH_FIELD_OFFSET = 0;

    private static final int LENGTH_FIELD_LENGTH = 4;

    private static final int INITIAL_BYTES_TO_STRIP = 0;

    private static final int LENGTH_ADJUSTMENT = 0;

    public ProxyMessageDecoder(){
        this(MAX_FRAME_SIZE,LEGNTH_FIELD_OFFSET,LENGTH_FIELD_LENGTH,LENGTH_ADJUSTMENT,INITIAL_BYTES_TO_STRIP);
    }

    public ProxyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    public ProxyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    public ProxyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    public ProxyMessageDecoder(ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    @Override
    protected DefaultProxyMessage decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 长度最小不能小于16
        if(in.readableBytes() < MessageConstant.HEAD_LENGTH){
            try{
                return decodeFrame(in);
            }catch (Exception e){
                log.error("decode error :{}",e.getMessage());
            }
        }
        return null;
    }

    private DefaultProxyMessage decodeFrame(ByteBuf in) {
        // 检查魔数是否正确
        checkMagicNumber(in);
        // 检查版本号是否正确
        checkVersion(in);
        // 获取消息总长度
        int fullMessageLength = in.readInt();
        // 获取编码类型
        byte codec = in.readByte();
        // 获取消息压缩格式
        byte compress = in.readByte();
        // 获取消息类型
        byte messageType = in.readByte();
        // 跳过填充的数据
        in.skipBytes(MessageConstant.FILL_NUMBER.length);

        DefaultProxyMessage message = DefaultProxyMessage
                .builder()
                .compress(compress)
                .codec(codec)
                .type(messageType)
                .build();
        // TODO 待优化
        if(message.getType() == RequestType.HEART_BEAT_REQUEST.getCode()){
            message.setData(MessageConstant.PING);
        }
        if(message.getType() == RequestType.HEART_BEAT_RESPONSE.getCode()){
            message.setData(MessageConstant.PONG);
        }

        int bodyLength = fullMessageLength - MessageConstant.HEAD_LENGTH;

        if(bodyLength > 0){
            byte[] body = new byte[bodyLength];

            in.readBytes(body);

            Serializer serializer = serializerFactory.getInstance(message.getCodec());

            Compress compresser = compressFactory.getInstance(message.getCompress());

            body = compresser.decompress(body);

            message.setData(serializer.deserialize(body, RequestType.getRequestType(message.getType())));
        }
        return message;
    }

    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();

        if(version!= MessageConstant.VERSION){
            log.error("not supported version :{}",version);
            throw new NotSupportedVersionException("not supported version");
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        byte[] bytes = new byte[MessageConstant.MAGIC_NUMBER.length];

        in.readBytes(bytes);

        for (int i = 0; i < bytes.length; i++) {
            if(bytes[i] != MessageConstant.MAGIC_NUMBER[i]){
                throw new NotSupportProtocolException("magic number error");
            }
        }
    }


}
