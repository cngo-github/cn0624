package org.example.service.dates.domain;

import java.util.concurrent.ExecutionException;

public interface DayValidate {
    public boolean isWeekend(String date);

    public boolean isHoliday(String date) throws ExecutionException;
}
