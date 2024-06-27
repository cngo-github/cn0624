package org.example.persistence.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.vavr.control.Option;
import java.time.Duration;
import java.util.List;
import lombok.NonNull;
import org.example.persistence.data.Holiday;
import org.example.persistence.gson.HolidayAdapter;
import org.example.persistence.gson.HolidayListAdapter;

public class HolidaysCacheDao {
  public static Duration DEFAULT_TIMEOUT = Duration.ofMinutes(5);

  protected final CacheDao cache;
  protected Gson g =
      new GsonBuilder()
          .registerTypeAdapter(
              new TypeToken<List<Holiday>>() {}.getType(), new HolidayListAdapter())
          .registerTypeAdapter(Holiday.class, new HolidayAdapter())
          .create();

  public HolidaysCacheDao(@NonNull CacheDao cache) {
    this.cache = cache;
  }

  public Option<List<Holiday>> get(int year) {
    String key = getKey(year);

    return cache
        .getOptional(key)
        .map(json -> g.fromJson(json, new TypeToken<List<Holiday>>() {}.getType()));
  }

  public String getKey(int year) {
    return String.format("holidays-%d", year);
  }

  public void store(int year, @NonNull List<Holiday> holidays) {
    cache.set(getKey(year), g.toJson(holidays), DEFAULT_TIMEOUT);
  }
}
