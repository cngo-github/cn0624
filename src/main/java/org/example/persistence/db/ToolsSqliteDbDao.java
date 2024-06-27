package org.example.persistence.db;

import static io.vavr.API.*;
import static io.vavr.Patterns.$Failure;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

public class ToolsSqliteDbDao implements ToolsDbDao {
  private static final Logger LOGGER = LoggerFactory.getLogger(ToolsDbDao.class);
  private final SqliteDbDao db;

  public ToolsSqliteDbDao(@NonNull String uri) throws SQLException {
    db = new SqliteDbDao(uri);
  }

  public Option<Tool> getTool(@NotNull ToolCode code) {
    String query = String.format("SELECT brand, code, type from tools WHERE code like '%s'", code);

    return Try.of(() -> db.query(query))
        .toOption()
        .map(
            rs -> {
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
            })
        .flatMap(prices -> Option.when(prices.size() == 1, prices.getFirst()));
  }

  public Option<RentalPrice> getPrice(@NotNull ToolType type) {
    String query =
        String.format(
            "SELECT type, dailyCharge, weekdayCharge, weekendCharge, holidayCharge FROM prices WHERE type like '%s'",
            type);

    return Try.of(() -> db.query(query))
        .toOption()
        .map(
            rs -> {
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
            })
        .flatMap(prices -> Option.when(prices.size() == 1, prices.getFirst()));
  }

  public Option<String> reserve(@NonNull ToolCode code) {
    String reservationId =
        String.format("%s||%s", UUID.randomUUID().toString().replace("-", ""), LocalDateTime.now());

    String query =
        String.format(
            "UPDATE tools SET reservedBy = '%s', reservedAt = '%s' WHERE id = (SELECT id FROM tools WHERE (reservedBy is null AND `code` LIKE '%s' AND `available` = 1) LIMIT 1)",
            reservationId, LocalDateTime.now(), code);

    return Try.of(() -> db.update(query))
        .onFailure(e -> LOGGER.error(String.format("Unable to reserve the tool %s.", code), e))
        .toOption()
        .flatMap(count -> Option.when(count == 1, reservationId));
  }

  public Either<Throwable, String> checkout(@NotNull String reservationId, @NotNull ToolType type) {
    String query =
        String.format(
            "UPDATE tools SET available = FALSE WHERE id = (SELECT id FROM tools WHERE (reservedBy LIKE '%s' AND `type` LIKE '%s'))",
            reservationId, type);

    Try<Integer> operation = Try.of(() -> db.update(query));
    return Match(operation)
        .of(
            Case($Failure($()), Either::left),
            Case(
                $(Success(0)),
                Either.left(
                    new CheckoutFailed(
                        String.format(
                            "The checkout of reservation %s for tool %s failed.",
                            reservationId, type)))),
            Case($(Success(1)), Either.right(reservationId)),
            Case(
                $(),
                Either.left(
                    new InvalidDatabaseState(
                        String.format(
                            "The database may be in an invalid state after the checkout reservation %s for tool %s.",
                            reservationId, type)))));
  }
}
