package org.example.persistence.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import lombok.NonNull;
import org.example.persistence.data.Holiday;

public class HolidayAdapter extends TypeAdapter<Holiday> {
  @Override
  public void write(@NonNull JsonWriter jsonWriter, @NonNull Holiday holiday) throws IOException {
    jsonWriter.beginObject();

    jsonWriter.name("name").value(holiday.getName());
    jsonWriter.name("observedOn").value(holiday.getObservedOn().toString());

    jsonWriter.endObject();
  }

  @Override
  public Holiday read(@NonNull JsonReader jsonReader) throws IOException {
    String tempName = "";
    String tempDate = "";

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
    return new Holiday(tempName, tempDate);
  }
}
