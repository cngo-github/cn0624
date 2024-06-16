package org.example.persistence.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.example.persistence.data.RentalPrice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.gson.stream.JsonToken.BEGIN_OBJECT;

public class RentalPriceListAdapter extends TypeAdapter<List<RentalPrice>> {
    @Override
    public void write(JsonWriter jsonWriter, List<RentalPrice> prices) throws IOException {
        jsonWriter.beginArray();

        for (RentalPrice price : prices) {
            jsonWriter.beginObject();

            jsonWriter.name("type").value(price.getType().toString());
            jsonWriter.name("dailyPrice").value(price.getDailyPrice().toString());
            jsonWriter.name("weekdayCharge").value(price.isWeekdayCharge());
            jsonWriter.name("weekendCharge").value(price.isWeekendCharge());
            jsonWriter.name("holidayCharge").value(price.isHolidayCharge());

            jsonWriter.endObject();
        }

        jsonWriter.endArray();
    }

    @Override
    public List<RentalPrice> read(JsonReader jsonReader) throws IOException {
        String tempType = "";
        float tempDailyPrice = Float.MAX_VALUE;
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
                RentalPrice p = new RentalPrice(tempType, tempDailyPrice, tempWeekdayCharge, tempWeekendCharge, tempHolidayCharge);
                prices.add(p);
            }
        }

        jsonReader.endArray();
        return prices;
    }
}
