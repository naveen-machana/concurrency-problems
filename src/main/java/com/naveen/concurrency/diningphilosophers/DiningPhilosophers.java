package com.naveen.concurrency.diningphilosophers;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/*
* Imagine you have five philosopher's sitting on a roundtable. The philosopher's do only two kinds of activities.
* One they contemplate, and two they eat. However, they have only five forks between themselves to eat their food with.
* Each philosopher requires both the fork to his left and the fork to his right to eat his food.
*
* Design a solution where each philosopher gets a chance to eat his food without causing a deadlock
*
* sol: allow only 4 diners at the max at any time
* */
public class DiningPhilosophers {
    private Semaphore[] forks = new Semaphore[5];
    private Semaphore maxDiners;
    Random random = new Random(System.currentTimeMillis());
    public DiningPhilosophers() {
        for (int i = 0; i < 5; i++) forks[i] = new Semaphore(1);
        maxDiners = new Semaphore(4);
    }

    public void lifeCycleForPhilosopher(int id) throws InterruptedException {
        while (true) {
            contemplate(id);
            eat(id);
        }
    }

    private void contemplate(int id) throws InterruptedException {
        System.out.println("Philosopher " + id + " is contemplating");
        TimeUnit.MILLISECONDS.sleep(random.nextInt(50));
    }

    private void eat(int id) throws InterruptedException {
        maxDiners.acquire();
        forks[id].acquire();
        forks[(id + 4) % 5].acquire();
        System.out.println("Philosopher " + id + " is eating");
        forks[id].release();
        forks[(id + 4) % 5].release();
        maxDiners.release();
    }

    private static void executeLifeCycleForThread(int id, DiningPhilosophers dp) {
        try {
            dp.lifeCycleForPhilosopher(id);
        } catch (Exception e) {}
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] philosophers = new Thread[5];
        DiningPhilosophers dp = new DiningPhilosophers();
        for (int i = 0; i < 5; i++) {
            int j = i;
            philosophers[i] = new Thread(() -> DiningPhilosophers.executeLifeCycleForThread(j, dp));
        }

        for (Thread t : philosophers) t.start();
        for (Thread t : philosophers) t.join();
    }
}
