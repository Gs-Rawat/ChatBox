package com.gsrawat.chatbox;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Observer, Runnable {
    private Socket client;
    private ArrayList<String> msgList;
    private BufferedReader reader;
    private DataInputStream dataReader;
    private DataOutputStream dataWriter;

    public Client(Socket client) {
        this.client = client;
        this.msgList = new ArrayList<>();
        this.reader = new BufferedReader(new InputStreamReader(System.in));
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
        MainServer mainServer = (MainServer) s;
        msgList.add(mainServer.getMessage());
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

    public String process(String msg) {
        return msg;
    }

    @Override
    public void run() {
        readProcessWrite();
    }
}
