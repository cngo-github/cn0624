package org.example.service.dates.domain;

import java.util.concurrent.ExecutionException;

public interface DayValidate {
    boolean isWeekend(String date);

    boolean isHoliday(String date) throws ExecutionException;
}
