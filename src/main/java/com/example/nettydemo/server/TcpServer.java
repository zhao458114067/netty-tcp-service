package com.example.nettydemo.server;

import com.example.nettydemo.coder.NettyDecoder;
import com.example.nettydemo.coder.NettyEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * @author ZhaoXu
 * @date 2022/11/22 16:42
 */
@Slf4j
public class TcpServer {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap server;
    private ChannelFuture channelFuture;
    private Integer port;

    public TcpServer(Integer port) {
        this.port = port;

        // nio连接处理池
        this.bossGroup = new NioEventLoopGroup();
        // 处理事件池
        this.workerGroup = new NioEventLoopGroup();
        server = new ServerBootstrap();
        server.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 自定义处理类
                        ch.pipeline().addLast(new NettyDecoder());
                        ch.pipeline().addLast(new NettyEncoder());
                        ch.pipeline().addLast(new TcpServerHandler());
                    }
                });
        server.option(ChannelOption.SO_BACKLOG, 128);
        server.childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    public synchronized void startListen() {
        try {
            // 绑定到指定端口
            channelFuture = server.bind(port).sync();
            log.info("netty服务器在[{}]端口启动监听", port);
        } catch (Exception e) {
            log.error("netty服务器在[{}]端口启动监听失败", port);
            e.printStackTrace();
        }
    }

    public void sendMessageToClient(String clientIp, Object msg) {
        Map<String, Channel> channelMap = TcpServerHandler.channelSkipMap.get(port);
        Channel channel = channelMap.get(clientIp);
        String sendStr;
        try {
            sendStr = OBJECT_MAPPER.writeValueAsString(msg);
        } catch (JsonGenerationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            log.info("向客户端 {} 发送消息内容：{}", clientIp, sendStr);
            channel.writeAndFlush(sendStr);
        } catch (Exception var4) {
            log.error("向客户端 {} 发送消息失败，消息内容：{}", clientIp, sendStr);
            throw new RuntimeException(var4);
        }
    }

    public void pushMessageToClients(Object msg) {
        Map<String, Channel> channelMap = TcpServerHandler.channelSkipMap.get(port);
        if (channelMap != null && !channelMap.isEmpty()) {
            channelMap.forEach((k, v) -> sendMessageToClient(k, msg));
        }
    }
}
