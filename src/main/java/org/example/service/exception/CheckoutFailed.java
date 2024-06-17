package org.example.service.exception;

import java.io.Serial;

public class CheckoutFailed extends Exception {
  @Serial private static final long serialVersionUID = 1L;

  public CheckoutFailed(String message) {
    super(message);
  }
}
