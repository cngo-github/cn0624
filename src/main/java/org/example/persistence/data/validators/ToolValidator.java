package org.example.persistence.data.validators;

import org.example.persistence.cache.CacheDao;
import org.example.persistence.data.Tool;

public class ToolValidator {
  private final CacheDao cache;

  public ToolValidator(CacheDao cache) {
    this.cache = cache;
  }

  public void isValidTool(Tool t) {
    //
  }
}
