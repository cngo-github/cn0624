package org.example.persistence.db.domain;

import lombok.NonNull;
import org.example.persistence.data.RentalPrice;
import org.example.persistence.data.Tool;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.data.enums.ToolType;

import java.util.Optional;

public interface ToolsDbDao {
    Optional<Tool> getTool(@NonNull ToolCode code) throws Exception;

    Optional<RentalPrice> getPrice(@NonNull ToolType type) throws Exception;

    Optional<String> reserve(@NonNull ToolCode code);

    void checkout(@NonNull String reservationId, @NonNull ToolType type) throws Exception;
}
