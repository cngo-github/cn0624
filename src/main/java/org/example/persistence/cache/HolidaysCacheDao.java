package org.example.persistence.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.persistence.data.Holiday;
import org.example.persistence.gson.HolidayListAdapter;

import java.time.Duration;
import java.util.List;

public class HolidaysCacheDao {
    public static Duration DEFAULT_TIMEOUT = Duration.ofMinutes(5);

    protected final CacheDao cache;
    protected Gson g =
            new GsonBuilder()
                    .registerTypeAdapter(
                            new TypeToken<List<Holiday>>() {
                            }.getType(), new HolidayListAdapter())
                    .create();

    public HolidaysCacheDao(CacheDao cache) {
        this.cache = cache;
    }

    public List<Holiday> get(int year) throws Exception {
        String key = getKey(year);

        if (!cache.exists(key)) {
            throw new CacheEntryNotFound(String.format("No cache entry found for key: %s", key));
        }

        String json = cache.get(key);
        return g.fromJson(json, new TypeToken<List<Holiday>>() {
        }.getType());
    }

    public String getKey(int year) {
        return String.format("holidays-%d", year);
    }

    public void store(int year, List<Holiday> holidays) {
        cache.set(String.valueOf(year), g.toJson(holidays), DEFAULT_TIMEOUT);
    }
}
