package org.example.service.data;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;
import lombok.Getter;
import lombok.NonNull;
import org.example.persistence.data.RentalPrice;
import org.example.persistence.data.Tool;
import org.example.persistence.data.enums.ToolBrand;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.data.enums.ToolType;
import org.example.service.dates.RentalDates;
import org.javamoney.moneta.Money;

public class RentalAgreement {
  @Getter private final ToolBrand toolBrand;
  @Getter private final ToolCode toolCode;
  @Getter private final ToolType toolType;
  @Getter private final long rentalDays;
  @Getter private final LocalDate checkoutDate;
  @Getter private final LocalDate dueDate;
  @Getter private final Money dailyCharge;
  @Getter private final long chargeDays;
  @Getter private final Money preDiscountCharge;
  @Getter private final int discountPercent;
  @Getter private final Money discountAmount;
  @Getter private final Money finalCharge;

  private final NumberFormat currencyFormatter;
  private final DateTimeFormatter dateTimeFormatter;

  public RentalAgreement(
      @NonNull Tool tool,
      @NonNull RentalDates dates,
      @NonNull RentalPrice price,
      int discountPercent) {
    this.toolBrand = tool.brand();
    this.toolCode = tool.code();
    this.toolType = tool.type();
    this.rentalDays = dates.getTotalDays();
    this.checkoutDate = dates.getCheckout();
    this.dueDate = dates.getDue();
    this.dailyCharge = price.dailyPrice();
    this.chargeDays =
        dates.getChargeDays(price.weekdayCharge(), price.holidayCharge(), price.weekendCharge());

    MonetaryContext currencyPrecision =
        MonetaryContextBuilder.of(Money.class)
            .set("java.lang.Class", Money.class)
            .set("precision", 3)
            .set("java.math.RoundingMode", RoundingMode.HALF_UP)
            .build();
    Money reducedPrecision = Money.of(0, "USD", currencyPrecision);

    this.preDiscountCharge = this.dailyCharge.multiply(this.chargeDays);
    this.discountPercent = discountPercent;
    this.discountAmount =
        reducedPrecision.add(this.preDiscountCharge).multiply(this.discountPercent).divide(100);
    this.finalCharge =
        reducedPrecision.add(this.discountAmount).negate().add(this.preDiscountCharge);

    this.currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    this.dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
  }

  public String getCheckoutDateStringFormatted() {
    return this.checkoutDate.format(dateTimeFormatter);
  }

  public String getDueDateStringFormatted() {
    return this.dueDate.format(dateTimeFormatter);
  }

  public String getDailyChargeStringFormatted() {
    return currencyFormatter.format(this.dailyCharge.getNumber());
  }

  public String getPrediscountChargeStringFormatted() {
    return currencyFormatter.format(this.preDiscountCharge.getNumber());
  }

  public String getDiscountAmountStringFormatted() {
    return currencyFormatter.format(this.discountAmount.getNumber());
  }

  public String getFinaLChargeStringFormatted() {
    return currencyFormatter.format(this.finalCharge.getNumber());
  }

  @Override
  public String toString() {
    return String.format(
        "Tool code: %s\nTool type: %s\nTool brand: %s\nRental days: %d\nCheckout date: %s\nDue date: %s\nDaily charge: %s\nCharge days: %d\nPre-discount charge: %s\nDiscount percent: %d%%\nDiscount amount: %s\nFinal charge: %s",
        this.toolCode,
        this.toolType,
        this.toolBrand,
        this.rentalDays,
        this.getCheckoutDateStringFormatted(),
        this.getDueDateStringFormatted(),
        this.getDailyChargeStringFormatted(),
        this.chargeDays,
        this.getPrediscountChargeStringFormatted(),
        this.discountPercent,
        this.getDiscountAmountStringFormatted(),
        this.getFinaLChargeStringFormatted());
  }
}
