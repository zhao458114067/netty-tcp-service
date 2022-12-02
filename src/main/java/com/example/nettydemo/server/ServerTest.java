package com.example.nettydemo.server;

/**
 * @author ZhaoXu
 * @date 2022/11/22 17:17
 */
public class ServerTest {
    public static void main(String[] args) {
        TcpServer tcpServer = new TcpServer(40001);
        tcpServer.startListen();
        while (true) {
            try {
                Thread.sleep(2000);
                tcpServer.pushMessageToClients("test");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
