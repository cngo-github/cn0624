package org.example.persistence.db;

import lombok.NonNull;
import org.example.persistence.data.RentalPrice;
import org.example.persistence.data.Tool;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.data.enums.ToolType;
import org.example.persistence.db.domain.ToolsDbDao;
import org.example.persistence.db.exception.InvalidDatabaseState;
import org.example.service.exception.CheckoutFailed;
import org.javamoney.moneta.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ToolsSqliteDbDao implements ToolsDbDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToolsDbDao.class);
    private final SqliteDbDao db;

    public ToolsSqliteDbDao(String uri) throws SQLException {
        db = new SqliteDbDao(uri);
    }

    public Optional<Tool> getTool(ToolCode code) throws Exception {
        Optional<Tool> tool = Optional.empty();
        String query = String.format("SELECT brand, code, type from tools WHERE code like '%s'", code);

        try (ResultSet rs = db.query(query)) {
            while (rs.next()) {
                String toolBrand = rs.getString("brand");
                String toolCode = rs.getString("code");
                String toolType = rs.getString("type");

                Tool t = new Tool(toolBrand, toolCode, toolType);

                if (tool.isEmpty()) {
                    tool = Optional.of(t);
                }
            }
        }

        return tool;
    }

    public List<Tool> getTools() throws Exception {
        List<Tool> tools = new ArrayList<>();
        String query = "SELECT DISTINCT brand, code, type from tools";

        try (ResultSet rs = db.query(query)) {
            while (rs.next()) {
                String brand = rs.getString("brand");
                String code = rs.getString("code");
                String type = rs.getString("type");

                Tool t = new Tool(brand, code, type);
                tools.add(t);
            }
        }

        return tools;
    }

    public Optional<RentalPrice> getPrice(ToolType type) throws Exception {
        Optional<RentalPrice> price = Optional.empty();
        String query =
                String.format(
                        "SELECT type, dailyCharge, weekdayCharge, weekendCharge, holidayCharge FROM prices WHERE type like '%s'",
                        type);

        try (ResultSet rs = db.query(query)) {
            while (rs.next()) {
                String priceType = rs.getString("type");
                String dailyCharge = rs.getString("dailyCharge");
                boolean weekdayCharge = rs.getBoolean("weekdayCharge");
                boolean weekendCharge = rs.getBoolean("weekendCharge");
                boolean holidayCharge = rs.getBoolean("holidayCharge");

                String[] s = dailyCharge.split(" ");

                RentalPrice p =
                        new RentalPrice(priceType, Money.of(Float.parseFloat(s[1]), s[0]), weekdayCharge, weekendCharge, holidayCharge);

                if (price.isEmpty()) {
                    price = Optional.of(p);
                }
            }
        }

        return price;
    }

    public List<RentalPrice> getPrices() throws Exception {
        List<RentalPrice> prices = new ArrayList<>();
        String query =
                "SELECT DISTINCT type, dailyCharge, weekdayCharge, weekendCharge, holidayCharge from prices";

        try (ResultSet rs = db.query(query)) {
            while (rs.next()) {
                String type = rs.getString("type");
                String dailyCharge = rs.getString("dailyCharge");
                boolean weekdayCharge = rs.getBoolean("weekdayCharge");
                boolean weekendCharge = rs.getBoolean("weekendCharge");
                boolean holidayCharge = rs.getBoolean("holidayCharge");

                String[] s = dailyCharge.split(" ");

                RentalPrice p =
                        new RentalPrice(type, Money.of(Float.parseFloat(s[1]), s[0]), weekdayCharge, weekendCharge, holidayCharge);
                prices.add(p);
            }
        }

        return prices;
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

    public void checkout(String reservationId, ToolType type) throws Exception {
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

    public void cleanup() throws Exception {
        db.connection.close();
    }
}
