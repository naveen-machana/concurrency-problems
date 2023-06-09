package com.naveen.concurrency;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeDeferredCallback {
    interface Callback {
        long getExecuteAt();

        void execute();
    }

    private static class CallbackImpl implements Callback, Comparable<CallbackImpl> {
        private long executeAt;
        private String name;

        public CallbackImpl(long delayInSeconds, String name) {
            this.name = name;
            this.executeAt = System.currentTimeMillis() + delayInSeconds * 1000;
        }

        public long getExecuteAt() {
            return executeAt;
        }

        public int compareTo(CallbackImpl o) {
            return (int) (this.executeAt - o.executeAt);
        }

        public void execute() {
            System.out.println("Task " + name + ", executed at : " + System.currentTimeMillis());
        }
    }

    interface Executor {
        void register(Callback cb);
    }

    private static class ExecutorImpl implements Executor {
        private Lock lock = new ReentrantLock();
        private Condition newCallbackArrived = lock.newCondition();
        private PriorityQueue<Callback> pq = new PriorityQueue<>();
        private Thread executor;

        @Override
        public void register(Callback cb) {
            lock.lock();
            pq.offer(cb);
            newCallbackArrived.signalAll();
            lock.unlock();
        }

        private void execute() throws InterruptedException {
            while (true) {
                lock.lock();
                while (pq.isEmpty()) {
                    newCallbackArrived.await();
                }

                while (!pq.isEmpty()) {
                    long sleepFor = pq.peek().getExecuteAt() - System.currentTimeMillis();
                    if (sleepFor < 0) break;
                    newCallbackArrived.await(sleepFor, TimeUnit.MILLISECONDS);
                }

                pq.poll().execute();
                lock.unlock();
            }
        }

        public void runTests() {
            executor = new Thread(() -> {
                try {
                    this.execute();
                } catch (InterruptedException e) {}
            });
            executor.start();
        }

    }
}
