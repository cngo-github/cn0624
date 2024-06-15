package org.example.persistence.data;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Holiday {
  private final String name;
  private final LocalDate observedOn;

  public Holiday(String name, String observedOn) throws DateTimeParseException {
    this.name = name;
    this.observedOn = LocalDate.parse(observedOn);
  }

  public String getName() {
    return name;
  }

  public LocalDate getObservedOn() {
    return observedOn;
  }

  @Override
  public String toString() {
    return String.format("Holiday(name: %s, observed on: %s)", name, observedOn);
  }
}
