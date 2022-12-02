package com.example.nettydemo.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ZhaoXu
 * @date 2022/11/8 13:48
 */
@Slf4j
public class TcpClientHandler extends SimpleChannelInboundHandler<String> {
    /**
     * 读取事件
     *
     * @param channelHandlerContext
     * @param msg
     */
    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) {
        log.info("服务返回消息 :{}", msg);
    }

    /**
     * 发生异常
     *
     * @param channelHandlerContext
     * @param throwable
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
        log.error("通信发生异常：" + throwable.getMessage());
        channelHandlerContext.close();
    }
}
