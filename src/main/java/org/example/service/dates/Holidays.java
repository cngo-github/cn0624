package org.example.service.dates;

import org.example.persistence.cache.CacheDao;
import org.example.persistence.cache.HolidaysCacheDao;

import java.util.concurrent.ExecutionException;

public abstract class Holidays {
    protected final HolidaysCacheDao cache;

    public Holidays(CacheDao cache) {
        this.cache = new HolidaysCacheDao(cache);
    }

    abstract public boolean isHoliday(String date) throws ExecutionException;
}
