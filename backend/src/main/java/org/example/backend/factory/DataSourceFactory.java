package org.example.backend.factory;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;

public class DataSourceFactory {
  public static DataSource getMySQLDataSource() {
    MysqlDataSource ds = new MysqlDataSource();

    String url = "jdbc:mysql://localhost:3306/hobbyhandwerker";
    String user = System.getenv("DB_USER");
    String password = System.getenv("DB_PASSWORD");

    if (user == null || password == null) {
      throw new IllegalStateException("Database-Env-Variables not set!");
    }

    ds.setURL(url);
    ds.setUser(user);
    ds.setPassword(password);

    return ds;
  }
}
