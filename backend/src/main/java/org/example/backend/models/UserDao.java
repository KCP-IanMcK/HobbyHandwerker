package org.example.backend.models;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
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
            u.setId(resultSet.getInt("ID_user"));
            u.setName(resultSet.getString("username"));
            u.setEmail(resultSet.getString("email"));
            u.setPassword(resultSet.getString("password"));

            user.add(u);
          }
          stmt.close();
          con.close();
        } catch (Exception e) {
          e.printStackTrace();
          con.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
      System.out.println(user + "1");
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

  @Override
  public User saveUser(User user) {
    int result = 0;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (Exception e) {
    }
    try (Connection con = getConnection("jdbc:mysql://localhost:3306/hobbyhandwerker", "linus", "Maria")) {

      try (PreparedStatement stmt = con.prepareStatement("INSERT INTO user (username, email, password) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, user.getName());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPassword());
        result = stmt.executeUpdate();
        if (result > 0) {
          try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
              user.setId(generatedKeys.getInt(1));
            }
          }
          return user;
        }
        stmt.close();
        con.close();
      } catch (Exception e) {
        e.printStackTrace();
        con.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return null;
  }
}



