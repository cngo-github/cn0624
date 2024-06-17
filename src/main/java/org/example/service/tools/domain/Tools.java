package org.example.service.tools.domain;

import java.util.concurrent.ExecutionException;
import org.example.persistence.cache.CacheDao;
import org.example.persistence.cache.ToolsCacheDao;

public abstract class Tools {
  protected final ToolsCacheDao cache;

  public Tools(CacheDao cache) {
    this.cache = new ToolsCacheDao(cache);
  }

  public abstract boolean isValidTool(String date) throws ExecutionException;
}
