package org.example.persistence.data;

public class Discount {
  public static boolean isValidDiscount(int discount) {
    return discount >= 0 && discount <= 100;
  }

  public static Discount create() {
    return new Discount();
  }

  public static Discount of(int discount) throws IllegalArgumentException {
    Discount d = new Discount();
    d.setPercent(discount);

    return d;
  }

  private int percent = 0;

  private Discount() {}

  public int getPercent() {
    return percent;
  }

  public void setPercent(int percent) throws IllegalArgumentException {
    if (!Discount.isValidDiscount(percent)) {
      throw new IllegalArgumentException("The discount must be an integer from 0 to 100.");
    }

    this.percent = percent;
  }

  public boolean isValid() {
    return Discount.isValidDiscount(percent);
  }
}
