package org.example.persistence.data;

import org.example.persistence.data.enums.ToolBrand;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.data.enums.ToolType;

public class Tool {
  private final ToolBrand brand;
  private final ToolCode code;
  private final ToolType type;

  public Tool(ToolBrand brand, ToolCode code, ToolType type) {
    this.brand = brand;
    this.code = code;
    this.type = type;
  }

  public ToolBrand getBrand() {
    return brand;
  }

  public ToolCode getCode() {
    return code;
  }

  public ToolType getType() {
    return type;
  }

  public boolean equals(Tool o) {
    return brand == o.getBrand() && code == o.getCode() && type == o.getType();
  }
}
