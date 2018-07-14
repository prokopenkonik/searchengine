package com.example.searchengine.tasks;

public abstract class AbstractSearchingTask implements Runnable {

    static final String EXIT = "exit";

    volatile boolean suspended;

    AbstractSearchingTask() {
    }

    public void suspend() {
        suspended = true;
    }

    public synchronized void resume() {
        suspended = false;
        notify();
    }
}
