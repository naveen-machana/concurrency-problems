package com.naveen.concurrency.superman;

/*
* You are designing a library of superheroes for a video game that your fellow developers will consume.
* Your library should always create a single instance of any of the superheroes and return the same instance to all the
* requesting consumers.
Say, you start with the class Superman. Your task is to make sure that other developers using your class can never
* instantiate multiple copies of superman. After all, there is only one superman!
* */
public class Superman {
    private Superman() {};

    public static Superman getSuperman() {
        return SupermanCreator.INSTANCE;
    }

    private static class SupermanCreator {
        private static Superman INSTANCE = new Superman();
    }
}
