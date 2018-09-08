package com.dewey.rpc.common.codec;

import com.dewey.rpc.common.serializer.protostuff.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 解码器
 * @author dewey
 * @date 2018/9/3 22:42
 */
public class RpcDecoder extends ByteToMessageDecoder{

    private static Logger logger = Logger.getLogger(RpcDecoder.class);

    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass){
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes()<4){
            logger.info("无相关内容解码");
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if(byteBuf.readableBytes()<dataLength){
            logger.info("解码信息有误");
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] date = new byte[dataLength];
        byteBuf.readBytes(date);
        list.add(SerializationUtil.deserialize(date,genericClass));
    }
}
