package de.thb.webbaki.service.Exceptions;

public class WrongPathException extends Exception{
    public WrongPathException() {
        super("This Path is not a valid option.");
    }

    public WrongPathException(String message) {
        super(message);
    }
}
