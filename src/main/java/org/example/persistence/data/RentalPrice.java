package org.example.persistence.data;

import java.math.BigDecimal;
import org.example.persistence.data.enums.ToolType;

public class RentalPrice {
  private final ToolType type;
  private final BigDecimal dailyCharge;
  private final boolean weekdayCharge;
  private final boolean weekendCharge;
  private final boolean holidayCharge;

  public RentalPrice(
      ToolType type,
      float dailyCharge,
      boolean weekdayCharge,
      boolean weekendCharge,
      boolean holidayCharge) {
    this.type = type;
    this.dailyCharge = Price.from(dailyCharge);
    this.weekdayCharge = weekdayCharge;
    this.weekendCharge = weekendCharge;
    this.holidayCharge = holidayCharge;
  }

  public BigDecimal getDailyCharge() {
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
