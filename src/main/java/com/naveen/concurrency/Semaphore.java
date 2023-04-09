package com.naveen.concurrency;

public class Semaphore {
    private final int maxPermits;
    private int available;

    public Semaphore(int maxPermits) {
        this.maxPermits = maxPermits;
        this.available = maxPermits;
    }

    public synchronized void acquire() {
        while (available == 0) { try { wait(); } catch(InterruptedException e) {}}
        available--;
        notifyAll();
    }

    public synchronized void release() {
        while (available == maxPermits) { try { wait(); } catch(InterruptedException e) {}}
        available++;
        notifyAll();
    }
}
