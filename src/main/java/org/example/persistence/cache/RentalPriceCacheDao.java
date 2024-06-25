package org.example.persistence.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.NonNull;
import org.example.persistence.data.RentalPrice;
import org.example.persistence.data.enums.ToolType;
import org.example.persistence.gson.RentalPriceAdapter;
import org.example.persistence.gson.RentalPriceListAdapter;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

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

    public RentalPriceCacheDao(@NonNull CacheDao cache) {
        this.cache = cache;
    }

    public Optional<RentalPrice> getPrice(@NonNull ToolType type) {
        String key = getKey(type);
        Optional<String> maybeJson = cache.getOptional(key);
        return maybeJson.map(j -> g.fromJson(j, RentalPrice.class));
    }

    public String getKey(@NonNull ToolType type) {
        return String.format("prices-%s", type);
    }

    public void store(@Nonnull RentalPrice rentalPrice) {
        cache.set(getKey(rentalPrice.type()), g.toJson(rentalPrice), DEFAULT_TIMEOUT);
    }
}
