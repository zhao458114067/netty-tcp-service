package com.example.nettydemo.client;

import com.example.nettydemo.coder.NettyDecoder;
import com.example.nettydemo.coder.NettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * @author ZhaoXu
 * @date 2022/11/8 13:39
 */
@Slf4j
public class TcpClient {
    private EventLoopGroup group;
    private ChannelFuture channelFuture;
    private final String ip;
    private final Integer port;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TcpClient(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * 建立连接
     *
     */
    public synchronized void connectServer() {
        log.info("开始建立连接，ip：{}， port：{}", ip, port);
        // 生命nio连接池
        this.group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            // 配置解码器以及消息处理类
            b.group(this.group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new NettyEncoder());
                            pipeline.addLast(new NettyDecoder());
                            pipeline.addLast(new TcpClientHandler());
                        }
                    });

            // 开始连接
            this.channelFuture = b.connect(ip, port).sync();
        } catch (Exception var4) {
            log.error("连接建立失败，ip：{}， port：{}", ip, port);
            this.group.shutdownGracefully();
            var4.printStackTrace();
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        this.group.shutdownGracefully();
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public synchronized void sendCommonMsg(Object msg) {
        String sendStr;
        if (!getConnectStatus()) {
            connectServer();
        }
        try {
            sendStr = objectMapper.writeValueAsString(msg);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonGenerationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            log.info("发送消息内容：{}", sendStr);
            this.channelFuture.channel().writeAndFlush(sendStr);
        } catch (Exception var4) {
            log.error("发送消息失败，消息内容：{}", sendStr);
            throw new RuntimeException(var4);
        }
    }

    /**
     * 获取当前连接状态
     */
    public Boolean getConnectStatus() {
        return group != null && !group.isShutdown() && !group.isShuttingDown();
    }
}
