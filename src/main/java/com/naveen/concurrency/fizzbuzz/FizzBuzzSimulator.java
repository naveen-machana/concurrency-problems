package com.naveen.concurrency.fizzbuzz;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class FizzBuzzSimulator {
    private enum FizzBuzzType {
        FIZZ, BUZZ, FIZZ_BUZZ, OTHER;
    }

    private static class FizzBuzz {
        private AtomicInteger generator;

        public FizzBuzz(AtomicInteger generator) {
            this.generator = generator;
        }

        public void fizz() throws InterruptedException {
            synchronized (generator) {
                while (!(generator.get() % 3 == 0 && generator.get() % 15 != 0)) generator.wait();
                System.out.println("Fizz: " + generator.getAndIncrement());
                TimeUnit.SECONDS.sleep(1);
                generator.notifyAll();
            }
        }

        public void buzz() throws InterruptedException {
            synchronized (generator) {
                while (!(generator.get() % 5 == 0 && generator.get() % 15 != 0)) generator.wait();
                System.out.println("Buzz: " + generator.getAndIncrement());
                TimeUnit.SECONDS.sleep(1);
                generator.notifyAll();
            }
        }

        public void fizzBuzz() throws InterruptedException {
            synchronized (generator) {
                while (generator.get() % 15 != 0) generator.wait();
                System.out.println("FizzBuzz: " + generator.getAndIncrement());
                TimeUnit.SECONDS.sleep(1);
                generator.notifyAll();
            }
        }

        public void other() throws InterruptedException {
            synchronized (generator) {
                while (generator.get() % 3 == 0 || generator.get() % 5 == 0 || generator.get() % 15 == 0) generator.wait();
                System.out.println("Other: " + generator.getAndIncrement());
                TimeUnit.SECONDS.sleep(1);
                generator.notifyAll();
            }
        }
    }

    private static class FizzBuzzRunnable implements Runnable {
        private FizzBuzz generator;
        FizzBuzzType type;

        public FizzBuzzRunnable(FizzBuzz generator, FizzBuzzType type) {
            this.generator = generator;
            this.type = type;
        }

        public void run() {
            while (true) {
                try {
                    if (type == FizzBuzzType.FIZZ) generator.fizz();
                    else if (type == FizzBuzzType.BUZZ) generator.buzz();
                    else if (type == FizzBuzzType.FIZZ_BUZZ) generator.fizzBuzz();
                    else generator.other();
                } catch (InterruptedException e) {}
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger generator = new AtomicInteger(1);
        FizzBuzz fizzBuzz = new FizzBuzz(generator);
        Thread t1 = new Thread(new FizzBuzzRunnable(fizzBuzz, FizzBuzzType.FIZZ));
        Thread t2 = new Thread(new FizzBuzzRunnable(fizzBuzz, FizzBuzzType.BUZZ));
        Thread t3 = new Thread(new FizzBuzzRunnable(fizzBuzz, FizzBuzzType.FIZZ_BUZZ));
        Thread t4 = new Thread(new FizzBuzzRunnable(fizzBuzz, FizzBuzzType.OTHER));
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
