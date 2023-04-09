package com.naveen.concurrency;

public class ReadWriteLock {
    private int readers;
    private boolean writeLocked;

    public synchronized void acquireReadLock() {
        while (writeLocked) { try{ wait(); } catch(InterruptedException e) {} }
        readers++;
        notifyAll();
    }

    public synchronized void releaseReadLock() {
        readers--;
        notifyAll();
    }

    public synchronized void acquireWriteLock() {
        while (writeLocked || readers != 0) { try { wait(); } catch (InterruptedException e) {}}
        writeLocked = true;
    }

    public synchronized void releaseWriteLock() {
        writeLocked = false;
        notifyAll();
    }
}
