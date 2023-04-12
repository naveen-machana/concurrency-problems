package com.naveen.concurrency.h2omolecule;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

public class H2OMolecule {

    private static class Molecule {
        int hcount = 0;
        int ocount = 0;
        Semaphore hpermits = new Semaphore(0);
        Semaphore opermits = new Semaphore(0);
        CyclicBarrier barrier = new CyclicBarrier(3);
        Lock lock = new ReentrantLock();

        public void hArrived() throws BrokenBarrierException, InterruptedException {
            boolean lastAtom = false;
            lock.lock();
            hcount++;

            if (hcount == 2 && ocount >= 1) {
                hcount -= 2;
                ocount -= 1;
                hpermits.release();
                opermits.release();
                lastAtom = true;
                lock.unlock();
            }
            else {
                lock.unlock();
                hpermits.acquire();
            }

            barrier.await();
            if (lastAtom) {
                System.out.println("H2O created");
            }
        }
        public void oArrived() throws InterruptedException, BrokenBarrierException {
            boolean lastAtom = false;
            lock.lock();
            ocount++;

            if (hcount >= 2 && ocount == 1) {
                hcount -= 2;
                ocount -= 1;
                hpermits.release(2);
                lastAtom = true;
                lock.unlock();
            }
            else {
                lock.unlock();
                opermits.acquire();
            }

            barrier.await();
            if (lastAtom) {
                System.out.println("H2O created");
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] hthreads = new Thread[10];
        Thread[] othreads = new Thread[5];
        Molecule m = new Molecule();

        for (int i = 0; i < 10; i++) hthreads[i] = new Thread(() -> { try { m.hArrived(); } catch(Exception e) {} });
        for (int i = 0; i < 5; i++) othreads[i] = new Thread(() -> { try { m.oArrived(); } catch(Exception e) {} });

        for (int i = 0; i < 10; i++) hthreads[i].start();
        for (int i = 0; i < 5; i++) othreads[i].start();

        for (int i = 0; i < 10; i++) hthreads[i].join();
        for (int i = 0; i < 5; i++) othreads[i].join();
    }
}
