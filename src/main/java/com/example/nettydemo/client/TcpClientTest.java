package com.example.nettydemo.client;

/**
 * @author ZhaoXu
 * @date 2022/11/22 17:19
 */
public class TcpClientTest {
    public static void main(String[] args) {
        TcpClient tcpClient = new TcpClient("127.0.0.1", 40001);
        tcpClient.connectServer();
        tcpClient.sendCommonMsg("asdasd");
    }
}
