package org.example.persistence.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.example.persistence.data.Holiday;

public class HolidayListAdapter extends TypeAdapter<List<Holiday>> {
  @Override
  public void write(JsonWriter jsonWriter, List<Holiday> holidays) throws IOException {
    jsonWriter.beginArray();

    for (Holiday holiday : holidays) {
      jsonWriter.beginObject();

      jsonWriter.name("name").value(holiday.getName());
      jsonWriter.name("observedOn").value(holiday.getObservedOn().toString());

      jsonWriter.endObject();
    }

    jsonWriter.endArray();
  }

  @Override
  public List<Holiday> read(JsonReader jsonReader) throws IOException {
    String tempName = "";
    String tempDate = "";

    List<Holiday> holidays = new LinkedList<>();
    jsonReader.beginArray();

    while (jsonReader.hasNext()) {
      switch (jsonReader.peek()) {
        case BEGIN_OBJECT -> {
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
    }

    jsonReader.endArray();
    return holidays;
  }
}
