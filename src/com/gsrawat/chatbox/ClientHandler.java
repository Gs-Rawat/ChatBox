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
    private int updateMsgCount;

    public ClientHandler(Socket client, int clientCode) {
        this.client = client;
        this.clientCode = clientCode;
        this.msgList = new ArrayList<>();
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.date = new Date();
        this.updateMsgCount = 0;
        setReaderWriter();
    }

    public void registerMe() {
        this.broadcastServer = BroadcastServer.getBroadCastServer();
        this.broadcastServer.registerObserver(this);
    }

    public void setReaderWriter() {
        try {
            this.dataReader = new DataInputStream(client.getInputStream());
            this.dataWriter = new DataOutputStream(client.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Subject s) {
        BroadcastServer broadcastServer = (BroadcastServer) s;
        msgList.add(broadcastServer.getMessage());
        ++updateMsgCount;
    }

    public void readProcessWrite() {
        try {
            while (true) {
                //read
                String msg = dataReader.readUTF();
                //process
                String out = process(msg);
                //write
                dataWriter.writeUTF(out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String manageLogin() {
        try {
            dataWriter.writeUTF("Enter your name: ");
            this.clientName = dataReader.readUTF();

            dataWriter.writeUTF("Enter you country: ");
            this.clientCountry = dataReader.readUTF();

        } catch (Exception e) {
            e.printStackTrace();
        }

        registerMe();
        return "Login successful " + "[" + clientName + " from " + clientCountry + "]";
    }

    public String sendMsg(String msg) {
        msg = msg.substring("send_msg:".length()).trim();

        if(msg.length() == 0) return "Can't send empty message";

        String time = date.toString();
        String start = "[" + clientName + ": " + time + "] ";
        broadcastServer.sendMessage(start + msg);
        --updateMsgCount;
        return "Message has been sent.";
    }

    public String getMsg() {
        StringBuilder builder = new StringBuilder();
        for (String str : msgList) {
            builder.append(str).append("\n");
        }
        updateMsgCount = 0;
        return builder.toString();
    }

    public String updateHandler() {
        if(updateMsgCount == 0) {
            return "No new messages. You are up to date.";
        }
        return "UPDATE: " + updateMsgCount + " new message.";
    }

    public String process(String msg) {
        msg = msg.trim();
        if (msg.startsWith("send_msg:")) {
            return sendMsg(msg);
        } else if (msg.startsWith("get_msg:")) {
            return getMsg();
        } else if (msg.startsWith("login:")) {
            return manageLogin();
        } else if(msg.startsWith("update:")) {
            return updateHandler();
        } else {
            return "Invalid command";
        }
    }

    @Override
    public void run() {
        readProcessWrite();
    }
}
