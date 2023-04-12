package com.naveen.concurrency.orderprinting;


import java.util.concurrent.Semaphore;

public class OrderedPrinting2 {
    private static class Printer {
        private final Semaphore one = new Semaphore(1);
        private final Semaphore two = new Semaphore(0);

        public void printFirst() throws InterruptedException {
            one.acquire();
            System.out.println("first");
            two.release();
        }

        public void printSecond() throws InterruptedException {
            two.acquire();
            System.out.println("second");
            one.release();
        }
    }

    private static class PrinterRunnable implements Runnable {
        private final int id;
        private final Printer p;

        public PrinterRunnable(int id, Printer p) {
            this.id = id;
            this.p = p;
        }

        public void run() {
            try {
                while (true) {
                    if (id == 1) p.printFirst();
                    else p.printSecond();
                }
            } catch (InterruptedException e) {}
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Printer p = new Printer();
        Thread t1 = new Thread(new PrinterRunnable(1, p));
        Thread t2 = new Thread(new PrinterRunnable(2, p));
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
