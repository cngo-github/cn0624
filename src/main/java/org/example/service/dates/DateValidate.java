package org.example.service.dates;

import java.time.DayOfWeek;
import java.time.LocalDate;
import org.example.service.dates.domain.DayValidate;
import org.example.service.dates.domain.Holidays;

public class DateValidate implements DayValidate {
  private final Holidays holidays;

  public DateValidate(Holidays holidays) {
    this.holidays = holidays;
  }

  public boolean isWeekend(LocalDate date) {
    ;
    DayOfWeek day = date.getDayOfWeek();

    return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
  }

  public boolean isHoliday(LocalDate date) {
    return holidays.isHoliday(date.toString());
  }
}
