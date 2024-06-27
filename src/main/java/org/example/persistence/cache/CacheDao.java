package org.example.persistence.cache;

import io.vavr.control.Option;
import java.time.Duration;
import lombok.NonNull;

/**
 * The interface for Cache data access objects.
 *
 * @author Chuong Ngo
 */
public interface CacheDao {
  /** Perform any necessary tear-down of the DAO. */
  default void cleanup() {}

  /**
   * Adds a string value, associating it to a string key to the cache.
   *
   * @param key the string to associate the value to.
   * @param value the value to cache.
   * @param timeout how long the value should stay valid.
   */
  void set(@NonNull String key, @NonNull String value, @NonNull Duration timeout);

  /**
   * Retrieves the value associated with the key and returns it as an Optional.
   *
   * @param key the key that is being queried.
   * @return The value associated with the key.
   */
  Option<String> getOptional(@NonNull String key);
}
