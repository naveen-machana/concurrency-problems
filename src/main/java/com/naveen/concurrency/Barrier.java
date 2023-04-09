package com.naveen.concurrency;

public class Barrier {
    int count, release, totalThreads;
    public Barrier(int c) { this.totalThreads = c; }
    public synchronized void arrive() throws InterruptedException {
        while (count == totalThreads) wait();
        count++;
        if (count == totalThreads) {
            notifyAll();
            release = totalThreads;
        }
        else while (count < totalThreads) wait();
        release--;
        if (release == 0) {
            count = 0;
            notifyAll();
        }
    }
}
