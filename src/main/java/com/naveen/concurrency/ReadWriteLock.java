package com.naveen.concurrency;

public class ReadWriteLock {
    private int readers;
    private boolean writeLocked;

    public synchronized void acquireReadLock() throws InterruptedException {
        while (writeLocked) wait();
        readers++;
    }

    public synchronized void releaseReadLock() {
        readers--;
        notifyAll();
    }

    public synchronized void acquireWriteLock() throws InterruptedException {
        while (writeLocked || readers != 0) wait();
        writeLocked = true;
    }

    public synchronized void releaseWriteLock() {
        writeLocked = false;
        notifyAll();
    }
}
