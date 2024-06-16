package org.example.persistence.db.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SqlDbDao {
    ResultSet query(String query) throws SQLException;

    void update(String update) throws SQLException;

    void cleanup() throws SQLException;
}
