package me.lorenzo0111.multilang.exceptions;

import me.lorenzo0111.multilang.data.StorageType;

public class DriverException extends RuntimeException {

    public DriverException(StorageType type) {
        super(String.format("Unable to load storage %s: driver %s not found. Please contact the plugin support.", type, type.getClassName()));
    }

}
