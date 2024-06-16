package org.example.persistence.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.persistence.data.Tool;
import org.example.persistence.gson.ToolAdapter;
import org.example.persistence.gson.ToolListAdapter;

import java.time.Duration;
import java.util.List;

public class ToolsCacheDao {
    public static Duration DEFAULT_TIMEOUT = Duration.ofMinutes(5);

    protected final CacheDao cache;
    protected Gson g =
            new GsonBuilder()
                    .registerTypeAdapter(
                            new TypeToken<List<Tool>>() {
                            }.getType(), new ToolListAdapter())
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

    public String getKey() {
        return "tools-%d";
    }

    public void store(List<Tool> tools) {
        cache.set(getKey(), g.toJson(tools), DEFAULT_TIMEOUT);
    }
}
