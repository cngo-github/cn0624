package org.example.persistence.db;

import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.NonNull;
import org.example.persistence.data.RentalPrice;
import org.example.persistence.data.Tool;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.data.enums.ToolType;
import org.example.persistence.db.domain.ToolsDbDao;
import org.example.persistence.db.exception.InvalidDatabaseState;
import org.example.service.exception.CheckoutFailed;
import org.javamoney.moneta.Money;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ToolsSqliteDbDao implements ToolsDbDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToolsDbDao.class);
    private final SqliteDbDao db;

    public ToolsSqliteDbDao(@NonNull String uri) throws SQLException {
        db = new SqliteDbDao(uri);
    }

    public Option<Tool> getTool(@NotNull ToolCode code) {
        String query = String.format("SELECT brand, code, type from tools WHERE code like '%s'", code);

        return Try.of(() -> db.query(query)).toOption().map(rs -> {
            List<Tool> tools = new ArrayList<>();

            try {
                while (rs.next()) {
                    String toolBrand = rs.getString("brand");
                    String toolCode = rs.getString("code");
                    String toolType = rs.getString("type");

                    Tool t = new Tool(toolBrand, toolCode, toolType);

                    tools.add(t);
                }
            } catch (SQLException ignored) {
            }

            return tools;
        }).flatMap(prices -> Option.when(prices.size() == 1, prices.getFirst()));
    }

    public Option<RentalPrice> getPrice(@NotNull ToolType type) {
        String query =
                String.format(
                        "SELECT type, dailyCharge, weekdayCharge, weekendCharge, holidayCharge FROM prices WHERE type like '%s'",
                        type);

        return Try.of(() -> db.query(query)).toOption().map(rs -> {
            List<RentalPrice> prices = new ArrayList<>();

            try {
                while (rs.next()) {
                    String priceType = rs.getString("type");
                    String dailyCharge = rs.getString("dailyCharge");
                    boolean weekdayCharge = rs.getBoolean("weekdayCharge");
                    boolean weekendCharge = rs.getBoolean("weekendCharge");
                    boolean holidayCharge = rs.getBoolean("holidayCharge");

                    String[] splitCharge = dailyCharge.split(" ");

                    RentalPrice p =
                            new RentalPrice(
                                    priceType,
                                    Money.of(Float.parseFloat(splitCharge[1]), splitCharge[0]),
                                    weekdayCharge,
                                    weekendCharge,
                                    holidayCharge);

                    prices.add(p);
                }
            } catch (SQLException ignored) {
            }

            return prices;
        }).flatMap(prices -> Option.when(prices.size() == 1, prices.getFirst()));
    }

    public Optional<String> reserve(@NonNull ToolCode code) {
        String reservationId =
                String.format("%s||%s", UUID.randomUUID().toString().replace("-", ""), LocalDateTime.now());

        String query =
                String.format(
                        "UPDATE tools SET reservedBy = '%s', reservedAt = '%s' WHERE id = (SELECT id FROM tools WHERE (reservedBy is null AND `code` LIKE '%s' AND `available` = 1) LIMIT 1)",
                        reservationId, LocalDateTime.now(), code);

        try {
            int rowsUpdated = db.update(query);

            if (rowsUpdated == 1) {
                return Optional.of(reservationId);
            }
        } catch (SQLException e) {
            LOGGER.error(String.format("Unable to reserve the tool %s.", code), e);
        }

        return Optional.empty();
    }

    public void checkout(@NotNull String reservationId, @NotNull ToolType type) throws Exception {
        String query =
                String.format(
                        "UPDATE tools SET available = FALSE WHERE id = (SELECT id FROM tools WHERE (reservedBy LIKE '%s' AND `type` LIKE '%s'))",
                        reservationId, type);
        int rowsUpdated = db.update(query);

        if (rowsUpdated == 0) {
            throw new CheckoutFailed(
                    String.format("The checkout of reservation %s for tool %s failed.", reservationId, type));
        } else if (rowsUpdated > 1) {
            throw new InvalidDatabaseState(
                    String.format(
                            "The database may be in an invalid state after the checkout reservation %s for tool %s.",
                            reservationId, type));
        }
    }
}
