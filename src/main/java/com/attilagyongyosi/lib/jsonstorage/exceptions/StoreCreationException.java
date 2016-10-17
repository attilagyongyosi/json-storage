package com.attilagyongyosi.lib.jsonstorage.exceptions;

public class StoreCreationException extends Exception {
    public StoreCreationException(final Throwable cause) {
        super(cause);
    }

    public StoreCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
