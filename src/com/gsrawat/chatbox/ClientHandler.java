package com.gsrawat.chatbox;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ClientHandler implements Observer, Runnable {
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    private Socket client;
    private ArrayList<String> msgList;
    private DataInputStream dataReader;
    private DataOutputStream dataWriter;
    private BroadcastServer broadcastServer;
    private boolean isAlive;
    private String clientName;
    private String clientCountry;
    private int clientCode;
    private Date date;
    private int updateMsgCount;

    public ClientHandler(Socket client, int clientCode) {
        this.client = client;
        this.clientCode = clientCode;
        this.msgList = new ArrayList<>();
        this.date = new Date();
        this.updateMsgCount = 0;
        setReaderWriter();
    }

    public void registerMe() {
        this.broadcastServer = BroadcastServer.getBroadCastServer();
        this.broadcastServer.registerObserver(this);
        this.isAlive = true;
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

                if(!isAlive) break;
            }

            // closing connection
            dataReader.close();
            dataWriter.close();
            client.close();
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
        // broadcast everyone
        broadcastServer.sendMessage(GREEN + clientName + " joined the chat" + RESET);
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
        if(msgList.isEmpty()) {
            return "Message box is empty.";
        }

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

    public String logout() {
        broadcastServer.removeObserver(this);
        //broadcast everyone
        broadcastServer.sendMessage(RED + clientName + " left the chat." + RESET);
        isAlive = false;
        return "Successfully logout";
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
        } else if(msg.startsWith("logout:")) {
            return logout();
        } else {
            return "Invalid command";
        }
    }

    @Override
    public void run() {
        readProcessWrite();
    }
}
