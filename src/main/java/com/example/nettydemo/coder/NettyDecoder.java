package com.example.nettydemo.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author ZhaoXu
 * @date 2022/11/22 17:03
 */
@Slf4j
public class NettyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int beginReader = in.readerIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.readerIndex(beginReader);
        } else {
            byte[] data = new byte[dataLength];
            in.readBytes(data);
            String str = new String(data, 0, dataLength, StandardCharsets.UTF_8);
            out.add(str);
        }
    }
}
