package com.naveen.concurrency;

import java.util.concurrent.TimeUnit;

public class UnisexBathroom {
    private static enum UsedBy { MEN, WOMEN, NONE };
    Semaphore maxAllowed = new Semaphore(3);
    UsedBy usedBy = UsedBy.NONE;
    int empsInBathroom;

    public void maleUseBathroom() throws InterruptedException {
        synchronized (this) {
            while (usedBy == UsedBy.WOMEN) wait();
            maxAllowed.acquire();
            empsInBathroom++;
            usedBy = UsedBy.MEN;
        }
        useBathroom();
        maxAllowed.release();

        synchronized (this) {
            empsInBathroom--;
            if (empsInBathroom == 0) usedBy = UsedBy.NONE;
            notifyAll();
        }
    }

    public void femaleUseBathroom() throws InterruptedException {
        synchronized (this) {
            while (usedBy == UsedBy.MEN) wait();
            maxAllowed.acquire();
            usedBy = UsedBy.WOMEN;
            empsInBathroom++;
        }
        useBathroom();
        maxAllowed.release();
        synchronized (this) {
            empsInBathroom--;
            if (empsInBathroom == 0) usedBy = UsedBy.NONE;
            notifyAll();
        }
    }

    private void useBathroom() throws InterruptedException {
        System.out.println(usedBy + " using bathroom. Number of employees using bathroom are " + empsInBathroom);
        TimeUnit.SECONDS.sleep(10);
        System.out.println("One employee done using bathroom");
    }
}
