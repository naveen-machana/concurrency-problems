package com.naveen.concurrency.barbershop;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/*
* A barbershop consists of a waiting room with n chairs, and a barber chair for giving haircuts.
* If there are no customers to be served, the barber goes to sleep. If a customer enters the barbershop and all chairs
* are occupied, then the customer leaves the shop. If the barber is busy, but chairs are available, then the customer
* sits in one of the free chairs. If the barber is asleep, the customer wakes up the barber. Write a program to
* coordinate the interaction between the barber and the customers.
* */
public class BarberShop {
    private Semaphore waitingChairs;
    private boolean isBarberChairAvailable;
    private Lock lock;
    private Condition customers;
    private Condition barber;
    private Semaphore hairCutDone;

    public BarberShop(int waitingChairs) {
        this.waitingChairs = new Semaphore(waitingChairs);
        this.isBarberChairAvailable = true;
        this.lock = new ReentrantLock();
        this.customers = this.lock.newCondition();
        barber = this.lock.newCondition();
        hairCutDone = new Semaphore(0);
    }

    public void customer() throws InterruptedException {
        if (waitingChairs.tryAcquire()) {
            try {
                lock.lock();
                while (!isBarberChairAvailable) barber.await();
                isBarberChairAvailable = false;
                customers.notify();
                lock.unlock();
                hairCutDone.acquire();
            } finally {
                waitingChairs.release();
            }
        }
    }

    private void groomhair() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(new Random().nextInt(50));
    }

    private void barber() throws InterruptedException {
        while (true) {
            lock.lock();
            while (isBarberChairAvailable) customers.await();
            groomhair();
            isBarberChairAvailable = true;
            barber.notifyAll();
            hairCutDone.release();
            lock.unlock();
        }
    }
}
