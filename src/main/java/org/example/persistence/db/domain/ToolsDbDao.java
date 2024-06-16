package org.example.persistence.db.domain;

import org.example.persistence.data.RentalPrice;
import org.example.persistence.data.Tool;

import java.util.List;

public interface ToolsDbDao {
    default void cleanup() throws Exception {
    }

    List<Tool> getTools() throws Exception;

    List<RentalPrice> getPrices() throws Exception;
}
