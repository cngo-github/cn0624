package org.example.persistence.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.NonNull;
import org.example.persistence.data.Tool;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.gson.ToolAdapter;
import org.example.persistence.gson.ToolListAdapter;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class ToolsCacheDao {
    public static Duration DEFAULT_TIMEOUT = Duration.ofMinutes(5);

    protected final CacheDao cache;
    protected Gson g =
            new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<List<Tool>>() {
                    }.getType(), new ToolListAdapter())
                    .registerTypeAdapter(Tool.class, new ToolAdapter())
                    .create();

    public ToolsCacheDao(@NonNull CacheDao cache) {
        this.cache = cache;
    }

    public Optional<Tool> get(@NonNull ToolCode code) {
        String key = this.getKeyTool(code);
        Optional<String> maybeJson = cache.getOptional(key);
        return maybeJson.map(j -> g.fromJson(j, Tool.class));
    }

    public void store(@Nonnull Tool tool) {
        cache.set(getKeyTool(tool.getCode()), g.toJson(tool), DEFAULT_TIMEOUT);
    }

    private String getKeyTool(@NonNull ToolCode code) {
        return String.format("tools-%s", code);
    }
}
