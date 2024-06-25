package org.example.persistence.data;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.NonNull;

public record Holiday(String name, LocalDate observedOn) {
  public Holiday(@NonNull String name, @NonNull String observedOn) throws DateTimeParseException {
    this(name, LocalDate.parse(observedOn));
  }

  @Override
  public String toString() {
    return String.format("Holiday(name: %s, observed on: %s)", name, observedOn);
  }
}
