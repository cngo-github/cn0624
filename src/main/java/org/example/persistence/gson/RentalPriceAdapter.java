package org.example.persistence.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.NonNull;
import org.example.persistence.data.RentalPrice;
import org.javamoney.moneta.Money;

import java.io.IOException;

public class RentalPriceAdapter extends TypeAdapter<RentalPrice> {
    @Override
    public void write(@NonNull JsonWriter jsonWriter, @NonNull RentalPrice price) throws IOException {
        jsonWriter.beginObject();

        jsonWriter.name("type").value(price.type().toString());
        jsonWriter.name("dailyPrice").value(price.dailyPrice().toString());
        jsonWriter.name("weekdayCharge").value(price.weekdayCharge());
        jsonWriter.name("weekendCharge").value(price.weekendCharge());
        jsonWriter.name("holidayCharge").value(price.holidayCharge());

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
