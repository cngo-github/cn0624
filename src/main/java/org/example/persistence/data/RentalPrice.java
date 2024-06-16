package org.example.persistence.data;

import org.example.persistence.data.enums.ToolType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RentalPrice {
    private final ToolType type;
    private final BigDecimal dailyPrice;
    private final boolean weekdayCharge;
    private final boolean weekendCharge;
    private final boolean holidayCharge;

    public RentalPrice(String type, float dailyPrice, boolean weekdayCharge, boolean weekendCharge, boolean holidayCharge) {
        this.type = ToolType.valueOf(type);

        BigDecimal bd = new BigDecimal(Float.toString(dailyPrice));

        this.dailyPrice = bd.setScale(2, RoundingMode.HALF_UP);
        this.weekdayCharge = weekdayCharge;
        this.weekendCharge = weekendCharge;
        this.holidayCharge = holidayCharge;
    }

    @Override
    public String toString() {
        return String.format("RentalPrice(type = %s, dailyCharge = $%s, weekdayCharge = %b, weekendCharge = %b, holidayCharge = %b)",
                type, dailyPrice, weekdayCharge, weekendCharge, holidayCharge);
    }

    public ToolType getType() {
        return type;
    }

    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }

    public boolean isWeekdayCharge() {
        return weekdayCharge;
    }

    public boolean isWeekendCharge() {
        return weekendCharge;
    }

    public boolean isHolidayCharge() {
        return holidayCharge;
    }
}
