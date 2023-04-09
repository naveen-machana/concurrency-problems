package com.naveen.concurrency.ratelimiter;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RaterLimiterInFIFOOrder {
    final int maxTokens;
    int available;
    ConcurrentLinkedQueue<String> q;
    long lastIssuedTime;
    AtomicInteger sequencer;

    public RaterLimiterInFIFOOrder(int maxTokens) {
        this.maxTokens = maxTokens;
        this.available = maxTokens;
        this.sequencer = new AtomicInteger();
        this.q = new ConcurrentLinkedQueue<>();
        this.lastIssuedTime = System.currentTimeMillis();
    }

    public void getToken() throws InterruptedException {
        q.offer(Thread.currentThread().getName());
        synchronized (this) {
            while (!q.peek().equals(Thread.currentThread().getName())) wait();
            long curTime = System.currentTimeMillis();
            available += (curTime - lastIssuedTime) / 1000;
            if (available > maxTokens) available = maxTokens;
            if (available == 0) TimeUnit.SECONDS.sleep(1);
            else available--;
            lastIssuedTime = System.currentTimeMillis();
            notifyAll();
            q.poll();
        }
    }
}
