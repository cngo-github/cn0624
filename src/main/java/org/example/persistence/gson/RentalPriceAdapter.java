package org.example.persistence.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.example.persistence.data.RentalPrice;

import java.io.IOException;

public class RentalPriceAdapter extends TypeAdapter<RentalPrice> {
    @Override
    public void write(JsonWriter jsonWriter, RentalPrice price) throws IOException {
        jsonWriter.beginObject();

        jsonWriter.name("type").value(price.getType().toString());
        jsonWriter.name("dailyPrice").value(price.getDailyPrice().toString());
        jsonWriter.name("weekdayCharge").value(price.isWeekdayCharge());
        jsonWriter.name("weekendCharge").value(price.isWeekendCharge());
        jsonWriter.name("holidayCharge").value(price.isHolidayCharge());

        jsonWriter.endObject();
    }

    @Override
    public RentalPrice read(JsonReader jsonReader) throws IOException {
        String tempType = "";
        float tempDailyPrice = Float.MAX_VALUE;
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
                    tempDailyPrice = Float.parseFloat(jsonReader.nextString());
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

        return new RentalPrice(tempType, tempDailyPrice, tempWeekdayCharge, tempWeekendCharge, tempHolidayCharge);
    }
}
