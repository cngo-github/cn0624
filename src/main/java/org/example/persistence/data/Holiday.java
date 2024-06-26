package org.example.persistence.data;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Holiday {
  private final String name;
  private final LocalDate observedOn;

  public Holiday(@NonNull String name, @NonNull String observedOn) throws DateTimeParseException {
    this.name = name;
    this.observedOn = LocalDate.parse(observedOn);
  }

  @Override
  public String toString() {
    return String.format("Holiday(name: %s, observed on: %s)", name, observedOn);
  }
}
