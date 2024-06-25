package org.example.persistence.data;

import lombok.NonNull;
import org.example.persistence.data.enums.ToolType;
import org.javamoney.moneta.Money;

public record RentalPrice(ToolType type, Money dailyPrice, boolean weekdayCharge, boolean weekendCharge,
                          boolean holidayCharge) {
    public RentalPrice(
            @NonNull String type,
            @NonNull Money dailyPrice,
            boolean weekdayCharge,
            boolean weekendCharge,
            boolean holidayCharge) {
        this(ToolType.valueOf(type), dailyPrice, weekdayCharge, weekendCharge, holidayCharge);
    }

    @Override
    public String toString() {
        return String.format(
                "RentalPrice(type = %s, dailyCharge = %s, weekdayCharge = %b, weekendCharge = %b, holidayCharge = %b)",
                type, dailyPrice, weekdayCharge, weekendCharge, holidayCharge);
    }
}
