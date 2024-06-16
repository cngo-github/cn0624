package org.example.persistence.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.example.persistence.data.Tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.gson.stream.JsonToken.BEGIN_OBJECT;

public class ToolListAdapter extends TypeAdapter<List<Tool>> {
    @Override
    public void write(JsonWriter jsonWriter, List<Tool> tools) throws IOException {
        jsonWriter.beginArray();

        for (Tool tool : tools) {
            jsonWriter.beginObject();

            jsonWriter.name("brand").value(tool.getBrand().toString());
            jsonWriter.name("code").value(tool.getCode().toString());
            jsonWriter.name("type").value(tool.getType().toString());

            jsonWriter.endObject();
        }

        jsonWriter.endArray();
    }

    @Override
    public List<Tool> read(JsonReader jsonReader) throws IOException {
        String tempBrand = "";
        String tempCode = "";
        String tempType = "";

        List<Tool> tools = new ArrayList<>();
        jsonReader.beginArray();

        while (jsonReader.hasNext()) {
            if (jsonReader.peek() == BEGIN_OBJECT) {
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
                Tool t = new Tool(tempBrand, tempCode, tempType);
                tools.add(t);
            }
        }

        jsonReader.endArray();
        return tools;
    }
}
