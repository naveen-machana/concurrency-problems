package com.naveen.concurrency.superman;

public class Superman2 {
    private Superman2() {}
    private static volatile Superman2 instance;

    public static Superman2 getInstance() {
        if (instance == null) {
            synchronized (Superman2.class) {
                if (instance == null) {
                    instance = new Superman2();
                }
            }
        }
        return instance;
    }
}
