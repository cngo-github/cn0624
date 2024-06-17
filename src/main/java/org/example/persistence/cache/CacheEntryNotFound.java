package org.example.persistence.cache;

import java.io.Serial;

public class CacheEntryNotFound extends Exception {
  @Serial private static final long serialVersionUID = 1L;

  public CacheEntryNotFound(String message) {
    super(message);
  }
}
