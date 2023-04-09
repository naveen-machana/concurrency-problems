package com.naveen.concurrency;

public class Barrier {
    private int max, count, released;
    public Barrier(int max) {
        this.max = max;
    }

    public synchronized void arrive() throws InterruptedException {
        while (count == max) wait();
        count++;
        if (count == max) {
            notifyAll();
            released = max;
        }
        else {
            while (count < max) wait();
        }
        released--;
        if (released == 0) {
            count = 0;
            notifyAll();
        }
    }
}
