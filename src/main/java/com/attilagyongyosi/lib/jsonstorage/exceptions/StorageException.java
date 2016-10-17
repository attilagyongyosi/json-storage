package com.attilagyongyosi.lib.jsonstorage.exceptions;

public class StorageException extends Exception {
    public StorageException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public StorageException(final Throwable cause) {
        super(cause);
    }
}
