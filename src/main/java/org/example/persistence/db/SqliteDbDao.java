package org.example.persistence.db;

import java.sql.*;
import org.example.persistence.db.domain.SqlDbDao;

public class SqliteDbDao implements SqlDbDao {
  public static int DEFAULT_TIMEOUT = 30;
  public final Connection connection;

  public SqliteDbDao(String uri) throws SQLException {
    this.connection = DriverManager.getConnection(uri);
  }

  public ResultSet query(String query) throws SQLException {
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(DEFAULT_TIMEOUT);
    return statement.executeQuery(query);
  }

  public int update(String update) throws SQLException {
    Statement statement = connection.createStatement();
    statement.setQueryTimeout(DEFAULT_TIMEOUT);

    return statement.executeUpdate(update);
  }

  public void cleanup() throws SQLException {
    connection.close();
  }
}
