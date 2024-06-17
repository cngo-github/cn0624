package org.example.service.dates.domain;

import java.time.LocalDate;
import lombok.NonNull;

public interface DayValidate {
  boolean isWeekend(@NonNull LocalDate date);

  boolean isHoliday(@NonNull LocalDate date);
}
