package com.naveen.concurrency;

public class CountDownLatch {
    private int max, cur;
    private boolean hold;
    public CountDownLatch(int max) {
        this.max = max;
        this.hold = true;
    }

    public synchronized void arrive() throws InterruptedException {
        cur++;
        while (cur != max && hold) wait();
        hold = false;
        System.out.println(Thread.currentThread().getName() + " has arrived");
        notifyAll();
    }
}
