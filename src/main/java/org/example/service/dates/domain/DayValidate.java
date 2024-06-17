package org.example.service.dates.domain;

import java.time.LocalDate;

public interface DayValidate {
    boolean isWeekend(LocalDate date);

    boolean isHoliday(LocalDate date);
}
