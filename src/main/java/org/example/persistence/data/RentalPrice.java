package org.example.persistence.data;

import lombok.Getter;
import lombok.NonNull;
import org.example.persistence.data.enums.ToolType;
import org.javamoney.moneta.Money;

@Getter
public class RentalPrice {
    private final ToolType type;
    private final Money dailyPrice;
    private final boolean weekdayCharge;
    private final boolean weekendCharge;
    private final boolean holidayCharge;

    public RentalPrice(
            @NonNull String type,
            @NonNull Money dailyPrice,
            boolean weekdayCharge,
            boolean weekendCharge,
            boolean holidayCharge) {
        this.type = ToolType.valueOf(type);
        this.dailyPrice = dailyPrice;
        this.weekdayCharge = weekdayCharge;
        this.weekendCharge = weekendCharge;
        this.holidayCharge = holidayCharge;
    }

    @Override
    public String toString() {
        return String.format(
                "RentalPrice(type = %s, dailyCharge = %s, weekdayCharge = %b, weekendCharge = %b, holidayCharge = %b)",
                type, dailyPrice, weekdayCharge, weekendCharge, holidayCharge);
    }
}
