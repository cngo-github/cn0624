package org.example.service.checkout;

import fj.data.Either;
import org.example.persistence.cache.RedisCacheDao;
import org.example.persistence.cache.RentalPriceCacheDao;
import org.example.persistence.cache.ToolsCacheDao;
import org.example.persistence.data.ValidationErrors;
import org.example.persistence.data.enums.ToolBrand;
import org.example.persistence.data.enums.ToolCode;
import org.example.persistence.data.enums.ToolType;
import org.example.persistence.db.SqliteDbDao;
import org.example.persistence.db.ToolsSqliteDbDao;
import org.example.persistence.db.domain.SqlDbDao;
import org.example.persistence.db.domain.ToolsDbDao;
import org.example.service.data.RentalAgreement;
import org.example.service.dates.DateValidate;
import org.example.service.dates.UsaHolidays;
import org.example.service.dates.domain.DayValidate;
import org.example.service.dates.domain.Holidays;
import org.junit.jupiter.api.*;
import redis.embedded.RedisServer;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCheckoutService {
    public static final String DATABASE_URI = "jdbc:sqlite:target/sample.db";
    public static final int REDIS_PORT = 12345;
    public static final String REDIS_URI = String.format("redis://localhost:%d", REDIS_PORT);
    public static String DATE_FORMAT_INPUT = "M/d/[yyyy][yy]";

    private static RedisServer REDIS_SERVER;
    private static SqlDbDao DATABASE_CONN;
    private static RedisCacheDao REDIS_CACHE;

    private CheckoutService checkoutService;

    @BeforeAll
    public static void setup() throws SQLException {
        REDIS_SERVER = new RedisServer(REDIS_PORT);
        REDIS_SERVER.start();

        DATABASE_CONN = new SqliteDbDao(DATABASE_URI);
        REDIS_CACHE = new RedisCacheDao(REDIS_URI);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        REDIS_CACHE.cleanup();
        DATABASE_CONN.cleanup();
        REDIS_SERVER.stop();
    }

    @BeforeEach
    public void setupConnections() throws SQLException {
        ToolsDbDao toolsDb = new ToolsSqliteDbDao(DATABASE_URI);

        ToolsCacheDao toolsCache = new ToolsCacheDao(REDIS_CACHE);
        RentalPriceCacheDao priceCache = new RentalPriceCacheDao(REDIS_CACHE);

        Holidays holidays = new UsaHolidays(REDIS_CACHE);
        DayValidate dayValidate = new DateValidate(holidays);

        checkoutService = new CheckoutService(toolsDb, toolsCache, priceCache, dayValidate);
    }

    @BeforeEach
    public void populateDatabase() throws SQLException {
        DATABASE_CONN.update("drop table if exists tools");
        DATABASE_CONN.update(
                "create table tools (id integer, brand string, code string, type string, reservedBy string, reservedAt datetime, available integer)");
        DATABASE_CONN.update(
                "insert into tools values(1, 'Stihl', 'CHNS', 'Chainsaw', null, null, TRUE)");
        DATABASE_CONN.update(
                "insert into tools values(2, 'Werner', 'LADW', 'Ladder', null, null, TRUE)");
        DATABASE_CONN.update(
                "insert into tools values(3, 'DeWalt', 'JAKD', 'Jackhammer', null, null, TRUE)");
        DATABASE_CONN.update(
                "insert into tools values(4, 'Ridgid', 'JAKR', 'Jackhammer', null, null, TRUE)");

        DATABASE_CONN.update("drop table if exists prices");
        DATABASE_CONN.update(
                "create table prices (id integer, type string, dailyCharge string, weekdayCharge integer, weekendCharge integer, holidayCharge integer)");
        DATABASE_CONN.update("insert into prices values(1, 'Ladder', 'USD 1.99', TRUE, TRUE, FALSE)");
        DATABASE_CONN.update("insert into prices values(2, 'Chainsaw', 'USD 1.49', TRUE, FALSE, TRUE)");
        DATABASE_CONN.update(
                "insert into prices values(3, 'Jackhammer', 'USD 2.99', TRUE, FALSE, FALSE)");
    }

    @AfterEach
    public void tearDownConnections() {
        checkoutService = null;
    }

    @Test
    public void test1() {
        ToolCode toolCode = ToolCode.JAKR;
        String dateStr = "9/3/15";
        LocalDate checkoutDate =
                LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT_INPUT));
        int rentalDaysInt = 5;
        Duration rentalDays = Duration.ofDays(rentalDaysInt);
        int discount = 101;

        Optional<Either<ValidationErrors, RentalAgreement>> result =
                checkoutService
                        .reserve(toolCode)
                        .map(r -> checkoutService.checkout(r, rentalDays, discount, checkoutDate));

        assertTrue(result.isPresent());
        assertTrue(result.get().isLeft());

        Optional<ValidationErrors> maybeErrors = result.map(e -> e.left().value());

        assertTrue(maybeErrors.isPresent());

        ValidationErrors errors = maybeErrors.get();
        assertEquals(1, errors.getErrors().size());
        assertEquals("The discount must be between 0 and 1", errors.toString());
    }

    @Test
    public void test2() {
        ToolCode toolCode = ToolCode.LADW;
        String dateStr = "7/2/20";
        LocalDate checkoutDate =
                LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT_INPUT));
        int rentalDaysInt = 3;
        Duration rentalDays = Duration.ofDays(rentalDaysInt);
        int discount = 10;

        Optional<Either<ValidationErrors, RentalAgreement>> result =
                checkoutService
                        .reserve(toolCode)
                        .map(r -> checkoutService.checkout(r, rentalDays, discount, checkoutDate));

        assertTrue(result.isPresent());
        assertTrue(result.get().isRight());

        Optional<RentalAgreement> maybeAgreement = result.map(e -> e.right().value());

        assertTrue(maybeAgreement.isPresent());

        RentalAgreement agreement = maybeAgreement.get();

        assertEquals(ToolCode.LADW, agreement.getToolCode());
        assertEquals(ToolType.Ladder, agreement.getToolType());
        assertEquals(ToolBrand.Werner, agreement.getToolBrand());
        assertEquals(3, agreement.getRentalDays());
        assertEquals("07/02/20", agreement.getCheckoutDateStringFormatted());
        assertEquals("07/05/20", agreement.getDueDateStringFormatted());
        assertEquals("$1.99", agreement.getDailyChargeStringFormatted());
        assertEquals(2, agreement.getChargeDays());
        assertEquals("$3.98", agreement.getPrediscountChargeStringFormatted());
        assertEquals(10, agreement.getDiscountPercent());
        assertEquals("$0.40", agreement.getDiscountAmountStringFormatted());
        assertEquals("$3.58", agreement.getFinaLChargeStringFormatted());

        String output =
                """
                        Tool code: LADW
                        Tool type: Ladder
                        Tool brand: Werner
                        Rental days: 3
                        Checkout date: 07/02/20
                        Due date: 07/05/20
                        Daily charge: $1.99
                        Charge days: 2
                        Pre-discount charge: $3.98
                        Discount percent: 10%
                        Discount amount: $0.40
                        Final charge: $3.58""";

        assertEquals(output, agreement.toString());
    }

    @Test
    public void test3() {
        ToolCode toolCode = ToolCode.CHNS;
        String dateStr = "7/2/15";
        LocalDate checkoutDate =
                LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT_INPUT));
        int rentalDaysInt = 5;
        Duration rentalDays = Duration.ofDays(rentalDaysInt);
        int discount = 25;

        Optional<Either<ValidationErrors, RentalAgreement>> result =
                checkoutService
                        .reserve(toolCode)
                        .map(r -> checkoutService.checkout(r, rentalDays, discount, checkoutDate));

        assertTrue(result.isPresent());
        assertTrue(result.get().isRight());

        Optional<RentalAgreement> maybeAgreement = result.map(e -> e.right().value());

        assertTrue(maybeAgreement.isPresent());

        RentalAgreement agreement = maybeAgreement.get();

        assertEquals(ToolCode.CHNS, agreement.getToolCode());
        assertEquals(ToolType.Chainsaw, agreement.getToolType());
        assertEquals(ToolBrand.Stihl, agreement.getToolBrand());
        assertEquals(5, agreement.getRentalDays());
        assertEquals("07/02/15", agreement.getCheckoutDateStringFormatted());
        assertEquals("07/07/15", agreement.getDueDateStringFormatted());
        assertEquals("$1.49", agreement.getDailyChargeStringFormatted());
        assertEquals(3, agreement.getChargeDays());
        assertEquals("$4.47", agreement.getPrediscountChargeStringFormatted());
        assertEquals(25, agreement.getDiscountPercent());
        assertEquals("$1.12", agreement.getDiscountAmountStringFormatted());
        assertEquals("$3.35", agreement.getFinaLChargeStringFormatted());

        String output =
                """
                        Tool code: CHNS
                        Tool type: Chainsaw
                        Tool brand: Stihl
                        Rental days: 5
                        Checkout date: 07/02/15
                        Due date: 07/07/15
                        Daily charge: $1.49
                        Charge days: 3
                        Pre-discount charge: $4.47
                        Discount percent: 25%
                        Discount amount: $1.12
                        Final charge: $3.35""";

        assertEquals(output, agreement.toString());
    }

    @Test
    public void test4() {
        ToolCode toolCode = ToolCode.JAKD;
        String dateStr = "9/3/15";
        LocalDate checkoutDate =
                LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT_INPUT));
        int rentalDaysInt = 6;
        Duration rentalDays = Duration.ofDays(rentalDaysInt);
        int discount = 0;

        Optional<Either<ValidationErrors, RentalAgreement>> result =
                checkoutService
                        .reserve(toolCode)
                        .map(r -> checkoutService.checkout(r, rentalDays, discount, checkoutDate));

        assertTrue(result.isPresent());
        assertTrue(result.get().isRight());

        Optional<RentalAgreement> maybeAgreement = result.map(e -> e.right().value());

        assertTrue(maybeAgreement.isPresent());

        RentalAgreement agreement = maybeAgreement.get();

        assertEquals(ToolCode.JAKD, agreement.getToolCode());
        assertEquals(ToolType.Jackhammer, agreement.getToolType());
        assertEquals(ToolBrand.DeWalt, agreement.getToolBrand());
        assertEquals(6, agreement.getRentalDays());
        assertEquals("09/03/15", agreement.getCheckoutDateStringFormatted());
        assertEquals("09/09/15", agreement.getDueDateStringFormatted());
        assertEquals("$2.99", agreement.getDailyChargeStringFormatted());
        assertEquals(3, agreement.getChargeDays());
        assertEquals("$8.97", agreement.getPrediscountChargeStringFormatted());
        assertEquals(0, agreement.getDiscountPercent());
        assertEquals("$0.00", agreement.getDiscountAmountStringFormatted());
        assertEquals("$8.97", agreement.getFinaLChargeStringFormatted());

        String output =
                """
                        Tool code: JAKD
                        Tool type: Jackhammer
                        Tool brand: DeWalt
                        Rental days: 6
                        Checkout date: 09/03/15
                        Due date: 09/09/15
                        Daily charge: $2.99
                        Charge days: 3
                        Pre-discount charge: $8.97
                        Discount percent: 0%
                        Discount amount: $0.00
                        Final charge: $8.97""";

        assertEquals(output, agreement.toString());
    }

    @Test
    public void test5() {
        ToolCode toolCode = ToolCode.JAKR;
        String dateStr = "7/2/15";
        LocalDate checkoutDate =
                LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT_INPUT));
        int rentalDaysInt = 9;
        Duration rentalDays = Duration.ofDays(rentalDaysInt);
        int discount = 0;

        Optional<Either<ValidationErrors, RentalAgreement>> result =
                checkoutService
                        .reserve(toolCode)
                        .map(r -> checkoutService.checkout(r, rentalDays, discount, checkoutDate));

        assertTrue(result.isPresent());
        assertTrue(result.get().isRight());

        Optional<RentalAgreement> maybeAgreement = result.map(e -> e.right().value());

        assertTrue(maybeAgreement.isPresent());

        RentalAgreement agreement = maybeAgreement.get();

        assertEquals(ToolCode.JAKR, agreement.getToolCode());
        assertEquals(ToolType.Jackhammer, agreement.getToolType());
        assertEquals(ToolBrand.Ridgid, agreement.getToolBrand());
        assertEquals(9, agreement.getRentalDays());
        assertEquals("07/02/15", agreement.getCheckoutDateStringFormatted());
        assertEquals("07/11/15", agreement.getDueDateStringFormatted());
        assertEquals("$2.99", agreement.getDailyChargeStringFormatted());
        assertEquals(5, agreement.getChargeDays());
        assertEquals("$14.95", agreement.getPrediscountChargeStringFormatted());
        assertEquals(0, agreement.getDiscountPercent());
        assertEquals("$0.00", agreement.getDiscountAmountStringFormatted());
        assertEquals("$14.95", agreement.getFinaLChargeStringFormatted());

        String output =
                """
                        Tool code: JAKR
                        Tool type: Jackhammer
                        Tool brand: Ridgid
                        Rental days: 9
                        Checkout date: 07/02/15
                        Due date: 07/11/15
                        Daily charge: $2.99
                        Charge days: 5
                        Pre-discount charge: $14.95
                        Discount percent: 0%
                        Discount amount: $0.00
                        Final charge: $14.95""";

        assertEquals(output, agreement.toString());
    }

    @Test
    public void test6() {
        ToolCode toolCode = ToolCode.JAKR;
        String dateStr = "7/2/20";
        LocalDate checkoutDate =
                LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT_INPUT));
        int rentalDaysInt = 4;
        Duration rentalDays = Duration.ofDays(rentalDaysInt);
        int discount = 50;

        Optional<Either<ValidationErrors, RentalAgreement>> result =
                checkoutService
                        .reserve(toolCode)
                        .map(r -> checkoutService.checkout(r, rentalDays, discount, checkoutDate));

        assertTrue(result.isPresent());
        assertTrue(result.get().isRight());

        Optional<RentalAgreement> maybeAgreement = result.map(e -> e.right().value());

        assertTrue(maybeAgreement.isPresent());

        RentalAgreement agreement = maybeAgreement.get();

        assertEquals(ToolCode.JAKR, agreement.getToolCode());
        assertEquals(ToolType.Jackhammer, agreement.getToolType());
        assertEquals(ToolBrand.Ridgid, agreement.getToolBrand());
        assertEquals(4, agreement.getRentalDays());
        assertEquals("07/02/20", agreement.getCheckoutDateStringFormatted());
        assertEquals("07/06/20", agreement.getDueDateStringFormatted());
        assertEquals("$2.99", agreement.getDailyChargeStringFormatted());
        assertEquals(1, agreement.getChargeDays());
        assertEquals("$2.99", agreement.getPrediscountChargeStringFormatted());
        assertEquals(50, agreement.getDiscountPercent());
        assertEquals("$1.50", agreement.getDiscountAmountStringFormatted());
        assertEquals("$1.49", agreement.getFinaLChargeStringFormatted());

        String output =
                """
                        Tool code: JAKR
                        Tool type: Jackhammer
                        Tool brand: Ridgid
                        Rental days: 4
                        Checkout date: 07/02/20
                        Due date: 07/06/20
                        Daily charge: $2.99
                        Charge days: 1
                        Pre-discount charge: $2.99
                        Discount percent: 50%
                        Discount amount: $1.50
                        Final charge: $1.49""";

        assertEquals(output, agreement.toString());
    }
}
