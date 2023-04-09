package com.naveen.concurrency;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

/*
* Imagine at the end of a political conference, republicans and democrats are trying to leave the venue and ordering
* Uber rides at the same time. However, to make sure no fight breaks out in an Uber ride, the software developers at
* Uber come up with an algorithm whereby either an Uber ride can have all democrats or republicans or two Democrats and
* two Republicans. All other combinations can result in a fist-fight.
*
* Your task as the Uber developer is to model the ride requestors as threads. Once an acceptable combination of riders
* is possible, threads are allowed to proceed to ride. Each thread invokes the method seated() when selected by the
* system for the next ride. When all the threads are seated, any one of the four threads can invoke the method drive()
* to inform the driver to start the ride.
* */
public class UberSeatingProblem {
    private int demsWaiting;
    private int repsWaiting;
    private CyclicBarrier barrier;
    private ReentrantLock lock;
    private Semaphore demsSemaphore;
    private Semaphore repsSemaphore;

    public UberSeatingProblem() {
        barrier = new CyclicBarrier(4);
        lock = new ReentrantLock();
        demsSemaphore = new Semaphore(0);
        repsSemaphore = new Semaphore(0);
    }

    public void seatDemocrat() throws BrokenBarrierException, InterruptedException {
        boolean rideLeader = false;
        lock.lock();
        demsWaiting++;

        if (demsWaiting == 4) {
            demsSemaphore.release(3);
            demsWaiting -= 4;
            rideLeader = true;
        }
        else if (demsWaiting == 2 && repsWaiting >= 2) {
            demsSemaphore.release(1);
            repsSemaphore.release(2);
            demsWaiting -= 2;
            repsWaiting -= 2;
            rideLeader = true;
        }
        else {
            lock.unlock();
            demsSemaphore.acquire();
        }

        seated();
        barrier.await();
        if (rideLeader) {
            drive();
            lock.unlock();
        }
    }

    public void drive() {
        System.out.println("Uber ride on its wayy. And ride leader is: " + Thread.currentThread().getName());
    }

    public void seatRupublican() throws InterruptedException, BrokenBarrierException {
        lock.lock();
        boolean rideLeader = false;
        repsWaiting++;
        if (repsWaiting == 4) {
            repsWaiting -= 4;
            repsSemaphore.release(3);
            rideLeader = true;
        }
        else if (repsWaiting == 2 && demsWaiting >= 2) {
            repsWaiting -= 1;
            demsWaiting -= 2;
            repsSemaphore.release(1);
            demsSemaphore.release(2);
            rideLeader = true;
        }
        else {
            lock.unlock();
            repsSemaphore.acquire();
        }
        seated();
        barrier.await();
        if (rideLeader) {
            drive();
            lock.unlock();
        }
    }
    public void seated() {
        System.out.println(Thread.currentThread().getName() + " seated");
    }
}
