package com.naveen.concurrency.orderprinting;

public class OrderedPrinting {
    private static class Printer {
        int turn = 0;

        public synchronized void printFirst() throws InterruptedException {
            while (turn == 1) wait();
            System.out.println("first");
            notifyAll();
            turn = 1;
        }

        public synchronized void printSecond() throws InterruptedException {
            while (turn == 0) wait();
            System.out.println("second");
            notifyAll();
            turn = 0;
        }
    }

    private static class PrinterRunnable implements Runnable {
        private int id;
        private Printer p;
        public PrinterRunnable(int id, Printer p) { this.id = id; this.p = p; }
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
