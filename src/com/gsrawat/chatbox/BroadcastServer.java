package com.gsrawat.chatbox;

import java.util.ArrayList;

public class BroadcastServer implements Subject {
    private static BroadcastServer broadcastServer = null;
    private ArrayList<Observer> list;
    private String message;

    private BroadcastServer() {
        this.list = new ArrayList<>();
    }

    @Override
    public void registerObserver(Observer o) {
        list.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        list.remove(o);
    }

    @Override
    public void notifyObserver() {
        for(Observer o: list) {
            o.update(this);
        }
    }

    public String getMessage() {
        return this.message;
    }

    public void sendMessage(String message) {
            this.message = message;
            notifyObserver();
    }

    public synchronized static BroadcastServer getBroadCastServer() {
        if (broadcastServer == null) {
            broadcastServer = new BroadcastServer();
        }
        return broadcastServer;
    }
}
