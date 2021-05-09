package com.gsrawat.chatbox;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ClientHandler implements Observer, Runnable {
    private Socket client;
    private ArrayList<String> msgList;
    private BufferedReader reader;
    private DataInputStream dataReader;
    private DataOutputStream dataWriter;
    private BroadcastServer broadcastServer;
    private String clientName;
    private String clientCountry;
    private int clientCode;
    private Date date;

    public ClientHandler(Socket client, int clientCode) {
        this.client = client;
        this.clientCode = clientCode;
        this.msgList = new ArrayList<>();
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.broadcastServer = BroadcastServer.getBroadCastServer();
        this.broadcastServer.registerObserver(this);
        this.date = new Date();
        setReaderWriter();
    }

    public void setReaderWriter() {
        try {
            this.dataReader = new DataInputStream(client.getInputStream());
            this.dataWriter = new DataOutputStream(client.getOutputStream());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Subject s) {
        BroadcastServer broadcastServer = (BroadcastServer) s;
        msgList.add(broadcastServer.getMessage());
    }

    public void readProcessWrite() {
        try {
            while(true) {
                //read
                String msg = dataReader.readUTF();
                //process
                String out = process(msg);
                //write
                dataWriter.writeUTF(out);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void manageLogin() {
        try {
            dataWriter.writeUTF("Enter your name: ");
            this.clientName = dataReader.readUTF();

            dataWriter.writeUTF("Enter you country: ");
            this.clientCountry = dataReader.readUTF();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String process(String msg) {
        StringBuilder builder = new StringBuilder();

        if(msg.startsWith("send_msg:")) {
            msg = msg.substring("send_msg:".length()).trim();
            String time = date.toString();
            String start = "[" + clientName + ": " + time + "] ";
            broadcastServer.sendMessage(start + msg);
            builder.append("Message Send.");
        }
        else if(msg.startsWith("get_msg:")) {
            for(String str : msgList) {
                builder.append(str).append("\n");
            }
        } else if(msg.startsWith("login:")) {
            manageLogin();
            builder.append("Login successful " + "[").append(clientName).append(" from ").append(clientCountry).append("]");
        }
        return builder.toString();
    }

    @Override
    public void run() {
        readProcessWrite();
    }
}
