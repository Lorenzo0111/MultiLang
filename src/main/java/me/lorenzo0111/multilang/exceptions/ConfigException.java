package me.lorenzo0111.multilang.exceptions;

public class ConfigException extends RuntimeException {

    public ConfigException(String message) {
        super(String.format("An error has occurred while parsing configuration: %s", message));
    }
}
