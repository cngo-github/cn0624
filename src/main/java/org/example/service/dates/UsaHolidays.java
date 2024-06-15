package org.example.service.dates;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.example.persistence.cache.CacheDao;
import org.example.persistence.cache.CacheEntryNotFound;
import org.example.persistence.data.Holiday;
import org.example.service.dates.domain.Holidays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UsaHolidays extends Holidays {
    public static String HOLIDAY_API = "https://date.nager.at/api/v3/PublicHolidays";
    public static List<String> HOLIDAY_NAMES = Arrays.asList("Independence Day", "Labour Day");
    public static String COUNTRY_CODE = "US";
    public static Duration DEFAULT_LOCAL_CACHE_EVICTION = Duration.ofHours(1);

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheDao.class);

    private final CacheLoader<Integer, List<Holiday>> cacheLoader =
            new CacheLoader<>() {
                @Override
                @Nonnull
                public List<Holiday> load(Integer key) throws Exception {
                    try {
                        return cache.get(key).stream().toList();
                    } catch (CacheEntryNotFound e) {
                        LOGGER.info(
                                String.format(
                                        "Unable to find the holidays for %s in the cache. Getting it from the API.",
                                        key));

                        List<Holiday> holidays = getFromApi(key);
                        cache.store(key, holidays);

                        return holidays;
                    }
                }
            };

    private final LoadingCache<Integer, List<Holiday>> holidays =
            CacheBuilder.newBuilder().expireAfterWrite(DEFAULT_LOCAL_CACHE_EVICTION).build(cacheLoader);

    public UsaHolidays(CacheDao cache) {
        super(cache);
    }

    public boolean isHoliday(String date) throws ExecutionException {
        LocalDate parsedDate = LocalDate.parse(date);
        int year = parsedDate.getYear();

        return holidays.get(year).stream().anyMatch(e -> e.getObservedOn().equals(parsedDate));
    }

    private List<Holiday> getFromApi(int year) throws IOException {
        String uri = String.format("%s/%d/%s", HOLIDAY_API, year, COUNTRY_CODE);

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(uri).build();

            return httpclient.execute(
                    httpGet,
                    response -> {
                        if (response.getCode() != 200) {
                            throw new IOException("Received an invalid response from the holidays API.");
                        }

                        String body = EntityUtils.toString(response.getEntity());

                        return JsonParser.parseString(body).getAsJsonArray().asList().stream()
                                .filter(
                                        e -> HOLIDAY_NAMES.contains(e.getAsJsonObject().get("localName").getAsString()))
                                .map(
                                        e -> {
                                            JsonObject o = e.getAsJsonObject();

                                            String name = o.get("localName").getAsString();
                                            String date = o.get("date").getAsString();

                                            return new Holiday(name, date);
                                        })
                                .toList();
                    });
        }
    }
}
