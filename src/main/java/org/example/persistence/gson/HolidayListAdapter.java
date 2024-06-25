package org.example.persistence.gson;

import static com.google.gson.stream.JsonToken.BEGIN_OBJECT;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.example.persistence.data.Holiday;

public class HolidayListAdapter extends TypeAdapter<List<Holiday>> {
  @Override
  public void write(@NonNull JsonWriter jsonWriter, @NonNull List<Holiday> holidays)
      throws IOException {
    jsonWriter.beginArray();

    for (Holiday holiday : holidays) {
      jsonWriter.beginObject();

      jsonWriter.name("name").value(holiday.name());
      jsonWriter.name("observedOn").value(holiday.observedOn().toString());

      jsonWriter.endObject();
    }

    jsonWriter.endArray();
  }

  @Override
  public List<Holiday> read(@NonNull JsonReader jsonReader) throws IOException {
    String tempName = "";
    String tempDate = "";

    List<Holiday> holidays = new ArrayList<>();
    jsonReader.beginArray();

    while (jsonReader.hasNext()) {
      if (jsonReader.peek() == BEGIN_OBJECT) {
        jsonReader.beginObject();

        while (jsonReader.hasNext()) {
          switch (jsonReader.nextName()) {
            case "name":
              tempName = jsonReader.nextString();
              continue;
            case "observedOn":
              tempDate = jsonReader.nextString();
          }
        }

        jsonReader.endObject();
        Holiday h = new Holiday(tempName, tempDate);
        holidays.add(h);
      }
    }

    jsonReader.endArray();
    return holidays;
  }
}
