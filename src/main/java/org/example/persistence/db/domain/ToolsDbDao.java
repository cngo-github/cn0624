package org.example.persistence.db.domain;

import java.util.List;
import java.util.Optional;
import org.example.persistence.data.RentalPrice;
import org.example.persistence.data.Tool;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.data.enums.ToolType;

public interface ToolsDbDao {
  default void cleanup() throws Exception {}

  Optional<Tool> getTool(ToolCode code) throws Exception;

  List<Tool> getTools() throws Exception;

  Optional<RentalPrice> getPrice(ToolType type) throws Exception;

  List<RentalPrice> getPrices() throws Exception;

  Optional<String> reserve(ToolCode code);

  void checkout(String reservationId, ToolType type) throws Exception;
}
