package de.thb.webbaki.service.helper;

/**
 * Is a helper for counting up an Integervalue from 0
 * Used in the templates
 */
public class Counter {
    private int value = 0;

    public int countAndGet(){
        return value++;
    }

    public int getValue() {
        return value;
    }
}
