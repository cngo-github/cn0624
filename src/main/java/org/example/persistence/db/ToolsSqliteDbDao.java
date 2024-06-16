package org.example.persistence.db;

import org.example.persistence.data.RentalPrice;
import org.example.persistence.data.Tool;
import org.example.persistence.db.domain.ToolsDbDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ToolsSqliteDbDao implements ToolsDbDao {
    private final SqliteDbDao db;

    public ToolsSqliteDbDao(String uri) throws SQLException {
        db = new SqliteDbDao(uri);
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

    public List<RentalPrice> getPrices() throws Exception {
        List<RentalPrice> prices = new ArrayList<>();
        String query = "SELECT DISTINCT type, dailyCharge, weekdayCharge, weekendCharge, holidayCharge from prices";

        try (ResultSet rs = db.query(query)) {
            while (rs.next()) {
                String type = rs.getString("type");
                float dailyCharge = rs.getFloat("dailyCharge");
                boolean weekdayCharge = rs.getBoolean("weekdayCharge");
                boolean weekendCharge = rs.getBoolean("weekendCharge");
                boolean holidayCharge = rs.getBoolean("holidayCharge");

                RentalPrice p = new RentalPrice(type, dailyCharge, weekdayCharge, weekendCharge, holidayCharge);
                prices.add(p);
            }
        }

        return prices;
    }

    public void cleanup() throws Exception {
        db.connection.close();
    }
}
