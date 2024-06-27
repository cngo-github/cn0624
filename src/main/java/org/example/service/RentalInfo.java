package org.example.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.NonNull;
import org.example.persistence.cache.RentalPriceCacheDao;
import org.example.persistence.cache.ToolsCacheDao;
import org.example.persistence.data.RentalPrice;
import org.example.persistence.data.Tool;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.data.enums.ToolType;
import org.example.persistence.db.domain.ToolsDbDao;
import org.example.service.exception.PriceUnavailable;
import org.example.service.exception.ToolUnvailable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class RentalInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(RentalInfo.class);

    public static Duration DEFAULT_LOCAL_CACHE_EVICTION = Duration.ofHours(1);

    private final ToolsCacheDao toolsCache;
    private final RentalPriceCacheDao pricesCache;
    private final ToolsDbDao toolsDb;

    private final CacheLoader<ToolCode, Tool> toolCacheLoader =
            new CacheLoader<>() {
                @NotNull
                @Override
                public Tool load(@NonNull ToolCode key) throws ToolUnvailable {
                    return toolsCache.get(key).orElse(() -> {
                        LOGGER.trace(
                                String.format(
                                        "Unable to get the tool %s from the cache. Trying the database.", key));

                        return toolsDb.getTool(key).map(t -> {
                            LOGGER.trace(String.format("Updating the cache for tool %s.", key));

                            toolsCache.store(t);
                            return t;
                        });
                    }).getOrElseThrow(() ->
                            new ToolUnvailable(String.format("The tool %s is unavailable.", key)));
                }
            };

    private final LoadingCache<ToolCode, Tool> toolMap =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(DEFAULT_LOCAL_CACHE_EVICTION)
                    .build(toolCacheLoader);

    private final CacheLoader<ToolType, RentalPrice> priceCacheLoader =
            new CacheLoader<>() {
                @NotNull
                @Override
                public RentalPrice load(@NonNull ToolType key) throws Exception {
                    return pricesCache.getPrice(key).orElse(() -> {
                        LOGGER.trace(
                                String.format(
                                        "Unable to get the rental price for %s from the cache. Trying the database.",
                                        key));

                        return toolsDb.getPrice(key).map(p -> {
                            LOGGER.trace(String.format("Updating the cache with the rental price for %s.", key));

                            pricesCache.store(p);
                            return p;
                        });
                    }).getOrElseThrow(() ->
                            new PriceUnavailable(String.format("The price for %s is unavailable.", key)));
                }
            };

    private final LoadingCache<ToolType, RentalPrice> priceMap =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(DEFAULT_LOCAL_CACHE_EVICTION)
                    .build(priceCacheLoader);

    public RentalInfo(
            @NonNull ToolsCacheDao toolsCache,
            @NonNull ToolsDbDao toolsDb,
            @NonNull RentalPriceCacheDao pricesCache) {
        this.toolsCache = toolsCache;
        this.toolsDb = toolsDb;
        this.pricesCache = pricesCache;
    }

    public Optional<Tool> getTool(@NonNull ToolCode code) {
        try {
            return Optional.of(this.toolMap.get(code));
        } catch (ExecutionException e) {
            LOGGER.error(String.format("unable to get the tool %s.", code), e);
        }

        return Optional.empty();
    }

    public Optional<RentalPrice> getPrice(@NonNull ToolType type) {
        try {
            return Optional.of(priceMap.get(type));
        } catch (ExecutionException e) {
            LOGGER.error(String.format("unable to get the rental price of %s.", type), e);
        }

        return Optional.empty();
    }

    public boolean validateTool(@NonNull Tool tool) {
        Tool t;

        try {
            t = toolMap.get(tool.code());
        } catch (ExecutionException e) {
            return false;
        }

        return tool.equals(t);
    }
}
