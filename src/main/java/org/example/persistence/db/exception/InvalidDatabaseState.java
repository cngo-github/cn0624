package org.example.persistence.db.exception;

import java.io.Serial;

public class InvalidDatabaseState extends Exception {
  @Serial private static final long serialVersionUID = 1L;

  public InvalidDatabaseState(String message) {
    super(message);
  }
}
