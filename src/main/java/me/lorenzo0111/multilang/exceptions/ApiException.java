package me.lorenzo0111.multilang.exceptions;

public class ApiException extends RuntimeException {

    public ApiException() {
        super("An error has occurred while running an api action.");
    }

    public ApiException(String message) {
        super(String.format("An error has occurred while running an api action: %s", message));
    }
}
