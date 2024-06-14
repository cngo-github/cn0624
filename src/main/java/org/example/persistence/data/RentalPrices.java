package org.example.persistence.data;

public class RentalPrices {
  private final float dailyCharge;
  private final boolean weekdayCharge;
  private final boolean weekendCharge;
  private final boolean holidayCharge;

  public RentalPrices(
      float dailyCharge, boolean weekdayCharge, boolean weekendCharge, boolean holidayCharge) {
    this.dailyCharge = dailyCharge;
    this.weekdayCharge = weekdayCharge;
    this.weekendCharge = weekendCharge;
    this.holidayCharge = holidayCharge;
  }

  public float getDailyCharge() {
    return dailyCharge;
  }

  public boolean isWeekdayCharge() {
    return weekdayCharge;
  }

  public boolean isWeekendCharge() {
    return weekendCharge;
  }

  public boolean isHolidayCharge() {
    return holidayCharge;
  }
}
