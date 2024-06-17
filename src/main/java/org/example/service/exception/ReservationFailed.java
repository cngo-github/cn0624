package org.example.service.exception;

import java.io.Serial;

public class ReservationFailed extends Exception {
  @Serial private static final long serialVersionUID = 1L;

  public ReservationFailed(String message) {
    super(message);
  }

  public ReservationFailed(String message, Exception e) {
    super(message, e);
  }
}
