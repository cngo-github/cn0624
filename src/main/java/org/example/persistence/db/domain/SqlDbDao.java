package org.example.persistence.db.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.NonNull;

public interface SqlDbDao {
  ResultSet query(@NonNull String query) throws SQLException;

  int update(@NonNull String update) throws SQLException;

  void cleanup() throws SQLException;
}
