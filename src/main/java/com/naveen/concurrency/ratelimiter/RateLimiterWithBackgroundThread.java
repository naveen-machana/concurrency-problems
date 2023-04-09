package com.naveen.concurrency.ratelimiter;

import java.util.concurrent.TimeUnit;

public class RateLimiterWithBackgroundThread {
    private interface RateLimiter {
        public void getToken();
    }

    private static class RateLimiterImpl implements RateLimiter {
        private final int MAX_TOKENS;
        private int available;
        private Thread tokenFiller;
        public RateLimiterImpl(int maxTokens) { this.MAX_TOKENS = maxTokens; }
        public synchronized void getToken() {
            while (available == 0) waitForTokens();
            available--;
            System.out.println("Token issued to "  + Thread.currentThread().getName());
        }

        private void waitForTokens() {
            try {
                this.wait();
            } catch (InterruptedException e) {}
        }

        private synchronized void fillToken() {

            while(true) {
                synchronized (this) {
                    if (available < MAX_TOKENS) available = MAX_TOKENS;
                    this.notifyAll();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {}
            }
        }

        public void initialize() {
            tokenFiller = new Thread(this::fillToken);
            tokenFiller.start();
        }
    }

    public class TokenBucketFactory {
        private TokenBucketFactory() {};
        public static RateLimiter getRateLimiter() {
            RateLimiterImpl limiter = new RateLimiterImpl(1);
            limiter.initialize();
            return limiter;
        }
    }

    public static void main(String[] args) {
        RateLimiter limiter = TokenBucketFactory.getRateLimiter();
        limiter.getToken();
    }
}
