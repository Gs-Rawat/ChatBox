package com.gsrawat.chatbox;

import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {
    private static int count = 0;

    public static void log(Socket client) {
        String ip = client.getInetAddress().getHostAddress();
        ++count;
        System.out.println(ip + ", [client count : " + count + "]");
    }

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(7000);
        System.out.println("Server successfully started at port : " + 7000);

        while(true) {
            Socket client = server.accept();
            log(client);

            ClientHandler observer = new ClientHandler(client, count);
            Thread thread = new Thread(observer);
            thread.start();
        }
    }
}
