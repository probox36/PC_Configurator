package Exceptions;

public class NoSlotsLeftException extends RuntimeException {
    public NoSlotsLeftException(String message) {
        super(message);
    }
}
