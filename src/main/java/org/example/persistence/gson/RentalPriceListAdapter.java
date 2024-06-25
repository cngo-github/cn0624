package org.example.persistence.gson;

import static com.google.gson.stream.JsonToken.BEGIN_OBJECT;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.example.persistence.data.RentalPrice;
import org.javamoney.moneta.Money;

public class RentalPriceListAdapter extends TypeAdapter<List<RentalPrice>> {
  @Override
  public void write(@NonNull JsonWriter jsonWriter, @NonNull List<RentalPrice> prices)
      throws IOException {
    jsonWriter.beginArray();

    for (RentalPrice price : prices) {
      jsonWriter.beginObject();

      jsonWriter.name("type").value(price.type().toString());
      jsonWriter.name("dailyPrice").value(price.dailyPrice().toString());
      jsonWriter.name("weekdayCharge").value(price.weekdayCharge());
      jsonWriter.name("weekendCharge").value(price.weekendCharge());
      jsonWriter.name("holidayCharge").value(price.holidayCharge());

      jsonWriter.endObject();
    }

    jsonWriter.endArray();
  }

  @Override
  public List<RentalPrice> read(@NonNull JsonReader jsonReader) throws IOException {
    String tempType = "";
    String tempDailyPrice = "";
    boolean tempWeekdayCharge = true;
    boolean tempWeekendCharge = true;
    boolean tempHolidayCharge = true;

    List<RentalPrice> prices = new ArrayList<>();
    jsonReader.beginArray();

    while (jsonReader.hasNext()) {
      if (jsonReader.peek() == BEGIN_OBJECT) {
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
        RentalPrice p =
            new RentalPrice(
                tempType,
                Money.of(Float.parseFloat(s[1]), s[0]),
                tempWeekdayCharge,
                tempWeekendCharge,
                tempHolidayCharge);
        prices.add(p);
      }
    }

    jsonReader.endArray();
    return prices;
  }
}
