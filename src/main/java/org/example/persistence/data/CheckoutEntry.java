package org.example.persistence.data;

import java.time.Duration;
import java.time.LocalDate;
import org.checkerframework.checker.index.qual.NonNegative;
import org.example.persistence.data.enums.ToolCode;

public class CheckoutEntry {
  private final ToolCode item;
  private final Duration rentTime;
  private final Discount discount;
  private final LocalDate date;

  public CheckoutEntry(ToolCode item, @NonNegative int rentDays, int discountPercent) {
    if (rentDays < 1) {
      throw new IllegalArgumentException("A tool must be rented for at least one day.");
    }

    this.item = item;
    this.rentTime = Duration.ofDays(rentDays);
    this.discount = Discount.of(discountPercent);
    this.date = LocalDate.now();
  }

  public ToolCode getItem() {
    return item;
  }

  public Discount getDiscount() {
    return discount;
  }

  public LocalDate getDate() {
    return date;
  }

  public Duration getRentTime() {
    return rentTime;
  }

  public String toString() {
    return String.format(
        "CheckoutEntry(tool code = %s, discount = %d percent, checkout duration = %s days, checkout date = %s)",
        item, discount.getPercent(), rentTime.toString(), date);
  }
}
