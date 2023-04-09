package com.naveen.concurrency.ratelimiter;

import java.util.concurrent.TimeUnit;

public class RaterLimiter {
    private final int MAX_TOKENS;
    private long lastTokenIssuedTime;
    private long available = 0;
    public RaterLimiter(int maxTokens) {
        MAX_TOKENS = maxTokens;
        lastTokenIssuedTime = System.currentTimeMillis();
    }

    public synchronized void getToken() throws InterruptedException {
        available += (System.currentTimeMillis() - lastTokenIssuedTime)/1000;
        if (available > MAX_TOKENS) available = MAX_TOKENS;
        if (available == 0) TimeUnit.SECONDS.sleep(1);
        else available--;
        lastTokenIssuedTime = System.currentTimeMillis();
        System.out.println("Granting " + Thread.currentThread().getName() + " token at " + lastTokenIssuedTime);
    }
}
