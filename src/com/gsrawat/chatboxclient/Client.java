package com.gsrawat.chatboxclient;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static final String YELLOW = "\u001B[33m";
    public static final String RESET = "\u001B[0m";

    private static DataInputStream dataReader;
    private static DataOutputStream dataWriter;
    private static BufferedReader reader;

    public static void main(String[] args) throws Exception {
        Socket client = new Socket("localhost", 7000);

        dataReader = new DataInputStream(client.getInputStream());
        dataWriter = new DataOutputStream(client.getOutputStream());
        reader = new BufferedReader(new InputStreamReader(System.in));

        String sendMsg = "";
        while(!sendMsg.equals(":exit")) {
            // read
            System.out.print(YELLOW + "> " + RESET);
            sendMsg = reader.readLine();

            // send
            dataWriter.writeUTF(sendMsg);

            // receive
            String out = dataReader.readUTF();
            System.out.println(out);
        }

        client.close();
        reader.close();
        dataReader.close();
        dataWriter.close();
    }
}
