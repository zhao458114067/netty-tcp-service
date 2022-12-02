package com.example.nettydemo.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author ZhaoXu
 * @date 2022/11/22 16:43
 */
@Slf4j
public class TcpServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 用跳表存储连接channel
     */
    public static Map<Integer, Map<String, Channel>> channelSkipMap = new ConcurrentSkipListMap<>();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("应用程序的监听通道异常!");
        cause.printStackTrace();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 获取每个用户端连接的ip
        InetSocketAddress ipSocket = (InetSocketAddress) channel.remoteAddress();
        String clientIp = ipSocket.getAddress().getHostAddress();
        InetSocketAddress localSocket = (InetSocketAddress) channel.localAddress();
        // 本地端口做键
        int localPort = localSocket.getPort();
        Map<String, Channel> channelMap = channelSkipMap.get(localPort);
        if (channelMap == null || channelMap.isEmpty()) {
            channelMap = new HashMap<>(4);
        }
        channelMap.put(clientIp, channel);
        channelSkipMap.put(localPort, channelMap);
        log.info("应用程序添加监听通道，与客户端：{} 建立连接成功！", clientIp);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 获取每个用户端连接的ip
        Channel channel = ctx.channel();
        InetSocketAddress localSocket = (InetSocketAddress) channel.localAddress();
        int localPort = localSocket.getPort();
        InetSocketAddress ipSocket = (InetSocketAddress) channel.remoteAddress();
        String clientIp = ipSocket.getAddress().getHostAddress();
        Map<String, Channel> channelMap = channelSkipMap.get(localPort);
        channelMap.remove(clientIp);
        log.info("应用程序移除监听通道，与客户端：{} 断开连接！", clientIp);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        Channel channel = channelHandlerContext.channel();
        // 获取每个用户端连接的ip
        InetSocketAddress ipSocket = (InetSocketAddress) channel.remoteAddress();
        log.info("接收到客户端： {} 应用数据：{}", ipSocket, msg);
    }
}
