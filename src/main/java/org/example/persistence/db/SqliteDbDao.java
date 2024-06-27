package org.example.persistence.db;

import java.sql.*;
import lombok.NonNull;
import org.example.persistence.data.enums.ToolType;
import org.example.persistence.db.domain.SqlDbDao;
import org.example.persistence.db.exception.InvalidDatabaseState;

public class SqliteDbDao implements SqlDbDao {
  public static InvalidDatabaseState INVALID_DATABASE_STATE_ERROR(
      @NonNull String reservationId, @NonNull ToolType type) {
    return new InvalidDatabaseState(
        String.format(
            "The database may be in an invalid state after the checkout reservation %s for tool %s.",
            reservationId, type));
  }

  public static int DEFAULT_TIMEOUT = 30;
  public final Connection connection;

  public SqliteDbDao(@NonNull String uri) throws SQLException {
    this.connection = DriverManager.getConnection(uri);
  }

  public ResultSet query(@NonNull String query) throws SQLException {
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(DEFAULT_TIMEOUT);
    return statement.executeQuery(query);
  }

  public int update(@NonNull String update) throws SQLException {
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(DEFAULT_TIMEOUT);

    return statement.executeUpdate(update);
  }

  public void cleanup() throws SQLException {
    connection.close();
  }
}
