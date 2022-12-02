package com.example.nettydemo.coder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author ZhaoXu
 * @date 2022/11/8 16:23
 */
public class NettyEncoder extends MessageToByteEncoder<String> {
    public NettyEncoder() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        byte[] byteMsg = msg.getBytes(StandardCharsets.UTF_8);
        int msgLength = byteMsg.length;
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(4 + byteMsg.length);
//        buf.order(ByteOrder.BIG_ENDIAN);
        buf.writeInt(msgLength);
        buf.writeBytes(byteMsg, 0, msgLength);
        out.writeBytes(buf);
        buf.release();
    }
}
