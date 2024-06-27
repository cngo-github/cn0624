package org.example.persistence.db.domain;

import io.vavr.control.Option;
import lombok.NonNull;
import org.example.persistence.data.RentalPrice;
import org.example.persistence.data.Tool;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.data.enums.ToolType;

import java.util.Optional;

public interface ToolsDbDao {
    Option<Tool> getTool(@NonNull ToolCode code);

    Option<RentalPrice> getPrice(@NonNull ToolType type);

    Optional<String> reserve(@NonNull ToolCode code);

    void checkout(@NonNull String reservationId, @NonNull ToolType type) throws Exception;
}
