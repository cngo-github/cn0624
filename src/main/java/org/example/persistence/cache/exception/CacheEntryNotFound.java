package org.example.persistence.cache.exception;

import java.io.Serial;

public class CacheEntryNotFound extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public CacheEntryNotFound(String message) {
        super(message);
    }
}
