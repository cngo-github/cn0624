package org.example.persistence.cache;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;

import java.time.Duration;
import java.util.Optional;

/**
 * An implementation of the cache DAO for REDIS.
 *
 * @author Chuong Ngo
 */
public class RedisCacheDao implements CacheDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheDao.class);

    private final JedisPooled clientPooled;

    public RedisCacheDao(String url) {
        clientPooled = new JedisPooled(url);

        LOGGER.trace("Initialized to url: {}", url);
    }

    public void set(@NotNull String key, @NotNull String value, @NotNull Duration timeout) {
        LOGGER.trace(
                "Get the value for the key: {} to value: {} with a timeout of {} seconds.",
                key,
                value,
                timeout);

        clientPooled.setex(key, timeout.getSeconds(), value);
    }

    public String get(@NotNull String key) {
        LOGGER.trace("Get the value for the key: {}.", key);

        return clientPooled.get(key);
    }

    public boolean exists(@NotNull String key) {
        LOGGER.trace("Does the key: {} exists in the cache?", key);

        return clientPooled.exists(key);
    }

    public Optional<String> getOptional(String key) {
        LOGGER.trace("Get the value for the key: {}.", key);

        return Optional.ofNullable(clientPooled.get(key));
    }

    @Override
    public void cleanup() {
        LOGGER.trace("Closing.");

        clientPooled.close();
    }
}
