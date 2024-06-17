package org.example.persistence.data;

import lombok.Getter;
import org.example.persistence.data.enums.ToolBrand;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.data.enums.ToolType;

@Getter
public class Tool {
  private final ToolBrand brand;
  private final ToolCode code;
  private final ToolType type;

  public Tool(String brand, String code, String type) {
    this.brand = ToolBrand.valueOf(brand);
    this.code = ToolCode.valueOf(code);
    this.type = ToolType.valueOf(type);
  }

  public boolean equals(Tool o) {
    return brand == o.getBrand() && code == o.getCode() && type == o.getType();
  }

  @Override
  public String toString() {
    return String.format("Tool(brand = %s, code = %s, type = %s)", brand, code, type);
  }
}
