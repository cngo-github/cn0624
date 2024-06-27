package org.example.service.dates.exception;

import java.io.Serial;

public class HolidaysUnavailable extends Exception {
  @Serial private static final long serialVersionUID = 1L;

  public HolidaysUnavailable(String message) {
    super(message);
  }
}
