package org.example.service.dates;

import lombok.Getter;
import lombok.NonNull;
import org.example.service.dates.domain.DayValidate;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class RentalDates {
    @Getter
    private final LocalDate checkout;
    @Getter
    private final LocalDate due;
    private final DayValidate dayValidate;
    @Getter
    private final long totalDays;

    private long holidaysCount = 0;
    private long weekendCount = 0;

    public RentalDates(
            @NonNull LocalDate checkout, @NonNull Duration days, @NonNull DayValidate dayValidate) {
        this.checkout = checkout;
        this.totalDays = days.toDays();
        this.due = checkout.plusDays(this.totalDays);
        this.dayValidate = dayValidate;

        this.calcHolidaysAndWeekends();
    }

    public long getChargeDays(boolean withWeekdays, boolean withHolidays, boolean withWeekends) {
        long chargeDays = withWeekdays ? this.totalDays : 0;

        if (!withHolidays) {
            chargeDays = chargeDays - this.holidaysCount;
        }

        if (!withWeekends) {
            chargeDays = chargeDays - this.weekendCount;
        }

        return chargeDays;
    }

    private void calcHolidaysAndWeekends() {
        List<LocalDate> dates = this.checkout.plusDays(1).datesUntil(this.due.plusDays(1)).toList();

        holidaysCount = dates.stream().map(dayValidate::isHoliday).filter(b -> b).count();
        weekendCount = dates.stream().map(dayValidate::isWeekend).filter(b -> b).count();
    }
}
