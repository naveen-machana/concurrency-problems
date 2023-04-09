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
* sol: Pick one of the philosopher to pick up his left fork first instead of right. Rest of the philosophers will
* pick up their right fork first and then left
* */
public class DiningPhilosophers2 {
    private Semaphore[] forks = new Semaphore[5];
    Random random = new Random(System.currentTimeMillis());
    public DiningPhilosophers2() {
        for (int i = 0; i < 5; i++) forks[i] = new Semaphore(1);
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
        if (id == 3) pickForkForLeftHanded(id);
        else pickForForRightHanded(id);
        System.out.println("Philosopher " + id + " is eating");
        forks[id].release();
        forks[(id + 4) % 5].release();
    }

    private void pickForkForLeftHanded(int id) throws InterruptedException {
        forks[(id + 4) % 5].acquire();
        forks[id].acquire();
    }

    private void pickForForRightHanded(int id) throws InterruptedException {
        forks[id].acquire();
        forks[(id + 4) % 5].acquire();
    }

    private static void executeLifeCycleForThread(int id, DiningPhilosophers2 dp) {
        try {
            dp.lifeCycleForPhilosopher(id);
        } catch (Exception e) {}
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] philosophers = new Thread[5];
        DiningPhilosophers2 dp = new DiningPhilosophers2();
        for (int i = 0; i < 5; i++) {
            int j = i;
            philosophers[i] = new Thread(() -> DiningPhilosophers2.executeLifeCycleForThread(j, dp));
        }

        for (Thread t : philosophers) t.start();
        for (Thread t : philosophers) t.join();
    }
}
