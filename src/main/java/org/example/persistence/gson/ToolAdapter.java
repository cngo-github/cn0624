package org.example.persistence.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import lombok.NonNull;
import org.example.persistence.data.Tool;

public class ToolAdapter extends TypeAdapter<Tool> {
  @Override
  public void write(@NonNull JsonWriter jsonWriter, @NonNull Tool tool) throws IOException {
    jsonWriter.beginObject();

    jsonWriter.name("brand").value(tool.getBrand().toString());
    jsonWriter.name("code").value(tool.getCode().toString());
    jsonWriter.name("type").value(tool.getType().toString());

    jsonWriter.endObject();
  }

  @Override
  public Tool read(@NonNull JsonReader jsonReader) throws IOException {
    String tempBrand = "";
    String tempCode = "";
    String tempType = "";

    jsonReader.beginObject();

    while (jsonReader.hasNext()) {
      switch (jsonReader.nextName()) {
        case "brand":
          tempBrand = jsonReader.nextString();
          continue;
        case "code":
          tempCode = jsonReader.nextString();
          continue;
        case "type":
          tempType = jsonReader.nextString();
      }
    }

    jsonReader.endObject();
    return new Tool(tempBrand, tempCode, tempType);
  }
}
