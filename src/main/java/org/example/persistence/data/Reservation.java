package org.example.persistence.data;

import lombok.Getter;

@Getter
public class Reservation {
  private final String id;
  private final Tool tool;
  private final RentalPrice price;

  public Reservation(String id, Tool tool, RentalPrice price) {
    this.id = id;
    this.tool = tool;
    this.price = price;
  }
}
