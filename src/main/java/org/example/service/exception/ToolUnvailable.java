package org.example.service.exception;

import java.io.Serial;

public class ToolUnvailable extends Exception {
  @Serial private static final long serialVersionUID = 1L;

  public ToolUnvailable(String message) {
    super(message);
  }
}
