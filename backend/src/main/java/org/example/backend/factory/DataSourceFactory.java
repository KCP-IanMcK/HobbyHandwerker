package org.example.backend.factory;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;

public class DataSourceFactory {
  public static DataSource getMySQLDataSource() {
    MysqlDataSource ds = new MysqlDataSource();
    ds.setURL("jdbc:mysql://localhost:3306/hobbyhandwerker");
    ds.setUser("admin");
    ds.setPassword("mariadb-pw-123"); //TODO env
    return ds;
  }
}
