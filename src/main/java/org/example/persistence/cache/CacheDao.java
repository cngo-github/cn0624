package org.example.persistence.cache;

import java.time.Duration;

/**
 * The interface for Cache data access objects.
 *
 * @author Chuong Ngo
 */
public interface CacheDao {
    /**
     * Perform any necessary setup of the DAO.
     */
    default void initialize() {
    }

    /**
     * Perform any necessary tear-down of the DAO.
     */
    default void cleanup() {
    }

    /**
     * Adds a string value, associating it to a string key to the cache.
     *
     * @param key     the string to associate the value to.
     * @param value   the value to cache.
     * @param timeout how long the value should stay valid.
     */
    void set(String key, String value, Duration timeout);

    /**
     * Checks if a key exists in the cache and has not timed out.
     *
     * @param key the key that is being queried.
     * @return true if the key is in the cache else false if the key is not in the cache or has timed
     * out.
     * @throws Exception there was a problem communicating with the cache.
     */
    boolean exists(String key) throws Exception;

    /**
     * Retrieves the value associated with the key.
     *
     * @param key the key that is being queried.
     * @return The value associated with the key.
     */
    String get(String key);
}