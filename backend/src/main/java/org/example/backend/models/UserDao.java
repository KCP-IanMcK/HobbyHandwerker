package org.example.backend.models;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.*;

public class UserDao implements IUserDao {

  @Override
  public List<User> select() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (Exception e) {
    }
    List<User> user = new ArrayList<>();
    try (Connection con = getConnection("jdbc:mysql://localhost:3306/hobbyhandwerker", "linus", "Maria")) {

      try (Statement stmt = con.createStatement()) {
        String tableSql = "SELECT * from user";
        try (ResultSet resultSet = stmt.executeQuery(tableSql)) {

          while (resultSet.next()) {

            User u = new User();
            u.setId(resultSet.getInt("id_user"));
            u.setName(resultSet.getString("username"));
            u.setEmail(resultSet.getString("email"));
            u.setPassword(resultSet.getString("password"));

            stmt.close();
            con.close();
          }
        } catch (Exception e) {
          e.printStackTrace();
          con.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
      return user;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public User select(int ID) {
    return null;
  }
}

