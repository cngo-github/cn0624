package org.example.persistence.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import lombok.NonNull;
import org.example.persistence.data.RentalPrice;
import org.javamoney.moneta.Money;

public class RentalPriceAdapter extends TypeAdapter<RentalPrice> {
  @Override
  public void write(@NonNull JsonWriter jsonWriter, @NonNull RentalPrice price) throws IOException {
    jsonWriter.beginObject();

    jsonWriter.name("type").value(price.getType().toString());
    jsonWriter.name("dailyPrice").value(price.getDailyPrice().toString());
    jsonWriter.name("weekdayCharge").value(price.isWeekdayCharge());
    jsonWriter.name("weekendCharge").value(price.isWeekendCharge());
    jsonWriter.name("holidayCharge").value(price.isHolidayCharge());

    jsonWriter.endObject();
  }

  @Override
  public RentalPrice read(@NonNull JsonReader jsonReader) throws IOException {
    String tempType = "";
    String tempDailyPrice = "";
    boolean tempWeekdayCharge = true;
    boolean tempWeekendCharge = true;
    boolean tempHolidayCharge = true;

    jsonReader.beginObject();

    while (jsonReader.hasNext()) {
      switch (jsonReader.nextName()) {
        case "type":
          tempType = jsonReader.nextString();
          continue;
        case "dailyPrice":
          tempDailyPrice = jsonReader.nextString();
          continue;
        case "weekdayCharge":
          tempWeekdayCharge = jsonReader.nextBoolean();
          continue;
        case "weekendCharge":
          tempWeekendCharge = jsonReader.nextBoolean();
          continue;
        case "holidayCharge":
          tempHolidayCharge = jsonReader.nextBoolean();
      }
    }

    jsonReader.endObject();

    String[] s = tempDailyPrice.split(" ");

    return new RentalPrice(
        tempType,
        Money.of(Float.parseFloat(s[1]), s[0]),
        tempWeekdayCharge,
        tempWeekendCharge,
        tempHolidayCharge);
  }
}
