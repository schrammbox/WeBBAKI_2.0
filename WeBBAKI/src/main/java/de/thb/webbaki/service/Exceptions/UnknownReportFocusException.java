package de.thb.webbaki.service.Exceptions;

public class UnknownReportFocusException extends Exception{
    public UnknownReportFocusException() {
        super("This Path is not a valid option.");
    }

    public UnknownReportFocusException(String message) {
        super(message);
    }
}
