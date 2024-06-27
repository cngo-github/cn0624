package org.example.persistence.db;

import io.vavr.control.Option;

public record CheckoutResult(Option<Throwable> e, Option<String> maybeReservationId) {
  public static CheckoutResult success(String reservationId) {
    return new CheckoutResult(reservationId);
  }

  public static CheckoutResult failiure(Throwable e) {
    return new CheckoutResult(e);
  }

  public CheckoutResult(Throwable e) {
    this(e == null ? Option.none() : Option.some(e), Option.none());
  }

  public CheckoutResult(String reservationId) {
    this(Option.none(), Option.some(reservationId));
  }
}
