package com.gsrawat.chatbox;

import java.util.ArrayList;

class BroadcastServer implements Subject {
    private final ArrayList<Observer> list;
    private String message;

    private BroadcastServer() {
        this.list = new ArrayList<>();
    }

    public static BroadcastServer getBroadCastServer() {
        return BroadcastServerHolder.INSTANCE;
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
        for (Observer o : list) {
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

    public ArrayList<Observer> getOnline() {
        return this.list;
    }

    private static class BroadcastServerHolder {
        private static final BroadcastServer INSTANCE = new BroadcastServer();
    }
}
