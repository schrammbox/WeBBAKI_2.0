package de.thb.webbaki.exception;

public class SnapshotNotFoundException extends RuntimeException {
    public SnapshotNotFoundException(String message) {
        super(message);
    }
}
