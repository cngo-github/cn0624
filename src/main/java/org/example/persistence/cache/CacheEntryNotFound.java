package org.example.persistence.cache;

public class CacheEntryNotFound extends Exception {
  public static final long serialVersionUID = 1L;

  public CacheEntryNotFound(String message) {
    super(message);
  }
}
