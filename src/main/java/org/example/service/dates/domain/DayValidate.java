package org.example.service.dates.domain;

import lombok.NonNull;

import java.time.LocalDate;

public interface DayValidate {
    boolean isWeekend(@NonNull LocalDate date);

    boolean isHoliday(@NonNull LocalDate date);
}
