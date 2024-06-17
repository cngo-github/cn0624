package org.example.persistence.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.persistence.data.Tool;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.gson.ToolAdapter;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class ToolsCacheDao {
    public static Duration DEFAULT_TIMEOUT = Duration.ofMinutes(5);

    protected final CacheDao cache;
    protected Gson g =
            new GsonBuilder()
                    .registerTypeAdapter(Tool.class, new ToolAdapter())
                    .create();

    public ToolsCacheDao(CacheDao cache) {
        this.cache = cache;
    }

    public List<Tool> get() throws Exception {
        String key = getKey();

        if (!cache.exists(key)) {
            throw new CacheEntryNotFound(String.format("No cache entry found for key: %s", key));
        }

        String json = cache.get(key);
        return g.fromJson(json, new TypeToken<List<Tool>>() {
        }.getType());
    }

    public Optional<Tool> getTool(ToolCode code) throws Exception {
        String key = this.getKeyTool(code);
        Optional<String> maybeJson = ((RedisCacheDao) cache).getOptional(key);
        return maybeJson.map(j -> g.fromJson(j, Tool.class));
    }

    public String getKey() {
        return "tools";
    }

    public void store(List<Tool> tools) {
        tools.forEach(t -> cache.set(getKeyTool(t.getCode()), g.toJson(t), DEFAULT_TIMEOUT));
    }

    public void store(@Nonnull Tool tool) {
        cache.set(getKeyTool(tool.getCode()), g.toJson(tool), DEFAULT_TIMEOUT);
    }

    private String getKeyTool(ToolCode code) {
        return String.format("tools-%s", code);
    }
}
