package com.naveen.concurrency.asyncToSync;

import java.util.HashSet;
import java.util.Set;

/*
* Imagine we have an Executor class that performs some useful task asynchronously via the method asynchronousExecution().
*  In addition the method accepts a callback object which implements the Callback interface. the object’s done() gets
* invoked when the asynchronous execution is done. The definition for the involved classes is below:
public class Executor {
    public void asynchronousExecution(Callback callback) throws Exception {
        Thread t = new Thread(() -> {
            // Do some useful work
            try {
            // Simulate useful work by sleeping for 5 seconds
                Thread.sleep(5000);
            } catch (InterruptedException ie) {
            }
            callback.done();
        });
        t.start();
    }
}

public interface Callback {
    public void done();
}

class Demonstration {
    public static void main( String args[] ) throws Exception{
        Executor executor = new Executor();
        executor.asynchronousExecution(() -> {
            System.out.println("I am done");
        });

        System.out.println("main thread exiting...");
    }
}

Note how the main thread exits before the asynchronous execution is completed.

Your task is to make the execution synchronous without changing the original classes
(imagine, you are given the binaries and not the source code) so that main thread waits till asynchronous execution is
complete. In other words, the highlighted line#8 only executes once the asynchronous task is complete.
* */
public class AsynchronousToSynchronousExecutor2 {
    private static class Executor {
        public void asynchronousExecution(Callback callback) throws Exception {
            Thread t = new Thread(() -> {
                // Do some useful work
                try {
                    // Simulate useful work by sleeping for 5 seconds
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                }
                callback.done();
            });
            t.start();
        }
    }

    public interface Callback {
        void done();
    }

    private static class SynchronousExecutor extends Executor {
        public void asynchronousExecution(Callback callback) throws Exception {
            Set<Boolean> completed = new HashSet<>();
            Object lock = new Object();
            Callback cb = () -> {
                callback.done();
                synchronized (lock) {
                    completed.add(true);
                    lock.notifyAll();
                }
            };
            super.asynchronousExecution(cb);

            synchronized (lock) {
                while (completed.isEmpty()) lock.wait();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Executor executor = new SynchronousExecutor();
        executor.asynchronousExecution(() -> {
            System.out.println("I am done");
        });
        System.out.println("main thread exiting...");
    }
}
