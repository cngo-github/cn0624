package org.example.service.dates;

import java.time.DayOfWeek;
import java.time.LocalDate;
import lombok.NonNull;
import org.example.service.dates.domain.DayValidate;
import org.example.service.dates.domain.Holidays;

public class DateValidate implements DayValidate {
  private final Holidays holidays;

  public DateValidate(@NonNull Holidays holidays) {
    this.holidays = holidays;
  }

  public boolean isWeekend(@NonNull LocalDate date) {
    DayOfWeek day = date.getDayOfWeek();

    return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
  }

  public boolean isHoliday(@NonNull LocalDate date) {
    return holidays.isHoliday(date.toString());
  }
}
