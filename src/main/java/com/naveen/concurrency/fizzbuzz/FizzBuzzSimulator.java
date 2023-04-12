package com.naveen.concurrency.fizzbuzz;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FizzBuzzSimulator {
    private static class Fizz implements Runnable {
        private AtomicInteger generator;
        public Fizz(AtomicInteger generator) {
            this.generator = generator;
        }
        public void run() {
            while (true) {
                synchronized (generator) {
                    while (!(generator.get() % 3 == 0 && generator.get() % 15 != 0)) {
                        try {
                            generator.wait();
                        } catch (InterruptedException e) {}
                    }
                    System.out.println("Fizz: " + generator.getAndIncrement());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    generator.notifyAll();
                }
            }
        }
    }

    private static class Buzz implements Runnable {
        private AtomicInteger generator;
        public Buzz(AtomicInteger generator) {
            this.generator = generator;
        }
        public void run() {
            while (true) {
                synchronized (generator) {
                    while (!(generator.get() % 5 == 0 && generator.get() % 15 != 0)) {
                        try {
                            generator.wait();
                        } catch (InterruptedException e) {}
                    }
                    System.out.println("Buzz: " + generator.getAndIncrement());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    generator.notifyAll();
                }
            }
        }
    }

    private static class FizzBuzz implements Runnable {
        private AtomicInteger generator;
        public FizzBuzz(AtomicInteger generator) {
            this.generator = generator;
        }
        public void run() {
            while (true) {
                synchronized (generator) {
                    while (generator.get() % 15 != 0) {
                        try {
                            generator.wait();
                        } catch (InterruptedException e) {}
                    }
                    System.out.println("FizzBuzz: " + generator.getAndIncrement());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    generator.notifyAll();
                }
            }
        }
    }

    private static class Other implements Runnable {
        private AtomicInteger generator;
        public Other(AtomicInteger generator) {
            this.generator = generator;
        }
        public void run() {
            while (true) {
                synchronized (generator) {
                    while (generator.get() % 3 == 0 || generator.get() % 5 == 0 || generator.get() % 15 == 0) {
                        try {
                            generator.wait();
                        } catch (InterruptedException e) {}
                    }
                    System.out.println("Other: " + generator.getAndIncrement());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    generator.notifyAll();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger generator = new AtomicInteger(1);
        Thread t1 = new Thread(new Fizz(generator));
        Thread t2 = new Thread(new Buzz(generator));
        Thread t3 = new Thread(new FizzBuzz(generator));
        Thread t4 = new Thread(new Other(generator));
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
    }
}
