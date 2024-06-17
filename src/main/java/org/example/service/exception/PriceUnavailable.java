package org.example.service.exception;

import java.io.Serial;

public class PriceUnavailable extends Exception {
  @Serial private static final long serialVersionUID = 1L;

  public PriceUnavailable(String message) {
    super(message);
  }
}
