package org.example.service.tools.domain;

import org.example.persistence.cache.CacheDao;
import org.example.persistence.cache.ToolsCacheDao;

import java.util.concurrent.ExecutionException;

public abstract class Tools {
    protected final ToolsCacheDao cache;

    public Tools(CacheDao cache) {
        this.cache = new ToolsCacheDao(cache);
    }

    abstract public boolean isValidTool(String date) throws ExecutionException;
}
