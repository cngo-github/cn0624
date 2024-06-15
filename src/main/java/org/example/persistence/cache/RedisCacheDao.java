package org.example.persistence.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;

import java.time.Duration;

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

    public void set(String key, String value, Duration timeout) {
        LOGGER.trace(
                "Get the value for the key: {} to value: {} with a timeout of {} seconds.",
                key,
                value,
                timeout);

        clientPooled.setex(key, timeout.getSeconds(), value);
    }

    public String get(String key) {
        LOGGER.trace("Get the value for the key: {}.", key);

        return clientPooled.get(key);
    }

    public boolean exists(String key) {
        LOGGER.trace("Does the key: {} exists in the cache?", key);

        return clientPooled.exists(key);
    }

    public void cleanup() {
        LOGGER.trace("Closing.");

        clientPooled.close();
    }
}
