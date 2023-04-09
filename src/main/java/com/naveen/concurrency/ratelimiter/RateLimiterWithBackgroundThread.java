package com.naveen.concurrency.ratelimiter;

import java.util.concurrent.TimeUnit;

public class RateLimiterWithBackgroundThread {
    private interface RateLimiter {
        public void getToken() throws InterruptedException;
    }

    private static class RateLimiterImpl implements RateLimiter {
        private final int MAX_TOKENS;
        private int available;
        private Thread tokenFiller;
        public RateLimiterImpl(int maxTokens) { this.MAX_TOKENS = maxTokens; this.available = MAX_TOKENS; }
        public synchronized void getToken() throws InterruptedException {
            while (available == 0) wait();
            available--;
            System.out.println("Token issued to "  + Thread.currentThread().getName());
        }

        private synchronized void fillToken() {

            while(true) {
                synchronized (this) {
                    available++;
                    if (available > MAX_TOKENS) available = MAX_TOKENS;
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

    public static void main(String[] args) throws InterruptedException {
        RateLimiter limiter = TokenBucketFactory.getRateLimiter();
        limiter.getToken();
    }
}
