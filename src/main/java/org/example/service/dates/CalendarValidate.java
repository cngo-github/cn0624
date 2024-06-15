package org.example.service.dates;

import org.example.service.dates.domain.DayValidate;
import org.example.service.dates.domain.Holidays;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

public class CalendarValidate implements DayValidate {
    private final Holidays holidays;

    public CalendarValidate(Holidays holidays) {
        this.holidays = holidays;
    }

    public boolean isWeekend(String date) {
        LocalDate parsedDate = LocalDate.parse(date);

        DayOfWeek day = parsedDate.getDayOfWeek();

        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public boolean isHoliday(String date) throws ExecutionException {
        return holidays.isHoliday(date);
    }
}
