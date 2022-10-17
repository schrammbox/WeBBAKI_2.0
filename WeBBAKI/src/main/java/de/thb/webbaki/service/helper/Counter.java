package de.thb.webbaki.service.helper;

public class Counter {
    private int value = 0;

    public int countAndGet(){
        return value++;
    }

    public int getValue() {
        return value;
    }
}
