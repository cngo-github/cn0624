package org.example.persistence.data;

import org.example.persistence.data.enums.ToolBrand;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.data.enums.ToolType;

public record Tool(ToolBrand brand, ToolCode code, ToolType type) {
    public Tool(String brand, String code, String type) {
        this(ToolBrand.valueOf(brand), ToolCode.valueOf(code), ToolType.valueOf(type));
    }

    public boolean equals(Tool o) {
        return brand == o.brand() && code == o.code() && type == o.type();
    }

    @Override
    public String toString() {
        return String.format("Tool(brand = %s, code = %s, type = %s)", brand, code, type);
    }
}
