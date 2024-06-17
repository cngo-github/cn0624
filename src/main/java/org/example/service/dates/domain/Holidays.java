package org.example.service.dates.domain;

import lombok.NonNull;
import org.example.persistence.cache.CacheDao;
import org.example.persistence.cache.HolidaysCacheDao;

public abstract class Holidays {
  protected final HolidaysCacheDao cache;

  public Holidays(@NonNull CacheDao cache) {
    this.cache = new HolidaysCacheDao(cache);
  }

  public abstract boolean isHoliday(@NonNull String date);
}
