package org.example.persistence.db.domain;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.NonNull;
import org.example.persistence.data.RentalPrice;
import org.example.persistence.data.Tool;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.data.enums.ToolType;

public interface ToolsDbDao {
  Option<Tool> getTool(@NonNull ToolCode code);

  Option<RentalPrice> getPrice(@NonNull ToolType type);

  Option<String> reserve(@NonNull ToolCode code);

  Either<Throwable, String> checkout(@NonNull String reservationId, @NonNull ToolType type);
}
