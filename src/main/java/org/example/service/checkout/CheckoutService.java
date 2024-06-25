package org.example.service.checkout;

import fj.data.Either;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.example.persistence.cache.RentalPriceCacheDao;
import org.example.persistence.cache.ToolsCacheDao;
import org.example.persistence.data.RentalPrice;
import org.example.persistence.data.Reservation;
import org.example.persistence.data.Tool;
import org.example.persistence.data.ValidationErrors;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.db.domain.ToolsDbDao;
import org.example.service.RentalInfo;
import org.example.service.data.RentalAgreement;
import org.example.service.dates.RentalDates;
import org.example.service.dates.domain.DayValidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckoutService {
  private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutService.class);

  private final ToolsDbDao toolsDb;
  private final RentalInfo info;
  private final DayValidate dayValidate;

  public CheckoutService(
      @NonNull ToolsDbDao toolsDb,
      @NonNull ToolsCacheDao toolsCache,
      @NonNull RentalPriceCacheDao pricesCache,
      @NonNull DayValidate dayValidate) {
    this.toolsDb = toolsDb;
    this.dayValidate = dayValidate;
    this.info = new RentalInfo(toolsCache, toolsDb, pricesCache);
  }

  public Optional<Reservation> reserve(@NonNull ToolCode code) {
    Optional<String> maybeReservationId = this.toolsDb.reserve(code);
    Optional<Tool> tool = this.info.getTool(code);
    Optional<RentalPrice> price = tool.flatMap(t -> this.info.getPrice(t.type()));

    return maybeReservationId
        .flatMap(id -> tool.map(t -> Pair.of(id, t)))
        .flatMap(t -> price.map(p -> new Reservation(t.getLeft(), t.getRight(), p)));
  }

  public Either<ValidationErrors, RentalAgreement> checkout(
      @NonNull Reservation reservation,
      @NonNull Duration days,
      int discount,
      @NonNull LocalDate date) {
    ValidationErrors errors = CheckoutDetailsValidator.validateCheckoutDetails(days, discount);

    if (!info.validateTool(reservation.tool())) {
      errors.add("The tool being checkout is invalid.");
    }

    if (errors.hasErrors()) {
      return Either.left(errors);
    }

    try {
      this.toolsDb.checkout(reservation.id(), reservation.tool().type());
    } catch (Exception e) {
      LOGGER.error(
          String.format(
              "Unable to checkout reservation %s for tool %s.",
              reservation.id(), reservation.tool().type()),
          e);

      errors.add(e.getMessage());
      return Either.left(errors);
    }

    RentalDates dates = new RentalDates(date, days, this.dayValidate);
    RentalAgreement agreement =
        new RentalAgreement(reservation.tool(), dates, reservation.price(), discount);

    return Either.right(agreement);
  }
}
