package org.example.persistence.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.persistence.data.RentalPrice;
import org.example.persistence.gson.RentalPriceAdapter;
import org.example.persistence.gson.RentalPriceListAdapter;

import java.time.Duration;
import java.util.List;

public class RentalPriceCacheDao {
    public static Duration DEFAULT_TIMEOUT = Duration.ofMinutes(5);

    protected final CacheDao cache;
    protected Gson g =
            new GsonBuilder()
                    .registerTypeAdapter(
                            new TypeToken<List<RentalPrice>>() {
                            }.getType(), new RentalPriceListAdapter())
                    .registerTypeAdapter(RentalPrice.class, new RentalPriceAdapter())
                    .create();

    public RentalPriceCacheDao(CacheDao cache) {
        this.cache = cache;
    }

    public List<RentalPrice> get() throws Exception {
        String key = getKey();

        if (!cache.exists(key)) {
            throw new CacheEntryNotFound(String.format("No cache entry found for key: %s", key));
        }

        String json = cache.get(key);
        return g.fromJson(json, new TypeToken<List<RentalPrice>>() {
        }.getType());
    }

    public String getKey() {
        return "prices-%d";
    }

    public void store(List<RentalPrice> prices) {
        cache.set(getKey(), g.toJson(prices), DEFAULT_TIMEOUT);
    }
}
