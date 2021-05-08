package com.gsrawat.chatbox;

import java.util.ArrayList;

public class MainServer implements Subject {
    private static MainServer mainServer = null;
    private ArrayList<Observer> list;
    private String message;

    private MainServer() {
        this.list = new ArrayList<>();
    }

    @Override
    public void registerObserver(Observer o) {
        list.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        list.add(o);
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

    public synchronized static MainServer getMainServer() {
        if (mainServer == null) {
            mainServer = new MainServer();
        }
        return mainServer;
    }
}
