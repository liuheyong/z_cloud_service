package com.cloud.service.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author: HeYongLiu
 * @create: 08-20-2019
 * @description: 简单socket测试
 **/
public class SocServer {

    private int port = 8181;

    SocServer() {
    }

    SocServer(int port) {
        this.port = port;
    }

    public void service() {
        try {
            ServerSocket server = new ServerSocket(port);
            Socket socket = server.accept();
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                boolean flag = true;
                int count = 1;
                System.out.println("第" + count + "次连接，");
                count++;
                while (flag) {
                    String line = input.readLine();
                    System.out.println(line);
                }
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SocServer().service();
    }
}
