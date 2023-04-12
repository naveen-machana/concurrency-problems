package com.naveen.concurrency.h2omolecule;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class H2OMolecule2 {

    private static class Molecule {
        int hcount = 0;
        int ocount = 0;
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        String[] molecule = new String[3];
        int count = 0;

        public void hArrived() throws InterruptedException {
            lock.lock();
            while (hcount == 2) condition.await();
            molecule[count++] = "H";
            hcount++;
            if (hcount == 2 && ocount == 1) {
                System.out.println(Arrays.toString(molecule));
                Arrays.fill(molecule, null);
                count = 0;
                hcount = 0;
                ocount = 0;
            }
            condition.signalAll();
            lock.unlock();
        }

        public void oArrived() throws InterruptedException {
            lock.lock();
            while (ocount == 1) condition.await();
            molecule[count++] = "O";
            ocount++;
            if (hcount == 2 && ocount == 1) {
                System.out.println(Arrays.toString(molecule));
                Arrays.fill(molecule, null);
                count = 0;
                hcount = 0;
                ocount = 0;
            }
            condition.signalAll();
            lock.unlock();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] hthreads = new Thread[10];
        Thread[] othreads = new Thread[5];
        Molecule m = new Molecule();

        for (int i = 0; i < 10; i++)
            hthreads[i] = new Thread(() -> {
                try {
                    m.hArrived();
                } catch (Exception e) {
                }
            });
        for (int i = 0; i < 5; i++)
            othreads[i] = new Thread(() -> {
                try {
                    m.oArrived();
                } catch (Exception e) {
                }
            });

        for (int i = 0; i < 10; i++) hthreads[i].start();
        for (int i = 0; i < 5; i++) othreads[i].start();

        for (int i = 0; i < 10; i++) hthreads[i].join();
        for (int i = 0; i < 5; i++) othreads[i].join();
    }
}
