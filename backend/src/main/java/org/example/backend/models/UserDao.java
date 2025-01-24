package org.example.backend.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    try (Connection con = getConnection("jdbc:mysql://localhost:3306/hobbyhandwerker", "adm_user", "the_password")) {

      try (Statement stmt = con.createStatement()) {
        String tableSql = "SELECT * from user";
        try (ResultSet resultSet = stmt.executeQuery(tableSql)) {

          resultSet.next();

          User u = new User();
          u.setId_user(resultSet.getInt("ID_user"));
          u.setUsername(resultSet.getString("username"));
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
  }

  @Override
  public User select(int ID) {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (Exception e) {
    }
    List<User> user = new ArrayList<>();
    try (Connection con = getConnection("jdbc:mysql://localhost:3306/hobbyhandwerker", "adm_user", "the_password")) {

      try (Statement stmt = con.createStatement()) {
        String tableSql = "SELECT * from user WHERE ID_user = ?;";
        try (PreparedStatement pstmt = con.prepareStatement(tableSql)) {

          pstmt.setInt(1, ID);

          try (ResultSet resultSet = pstmt.executeQuery()) {

            while (resultSet.next()) {
              User u = new User();
              u.setId_user(resultSet.getInt("ID_user"));
              u.setUsername(resultSet.getString("username"));
              u.setEmail(resultSet.getString("email"));
              u.setPassword(resultSet.getString("password"));

              stmt.close();
              con.close();
              return u;
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          con.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return null;
  }

  @Override
  public User saveUser(User user) {
    int result = 0;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (Exception e) {
    }
    try (Connection con = getConnection("jdbc:mysql://localhost:3306/hobbyhandwerker", "adm_user", "the_password")) {

      try (PreparedStatement stmt = con.prepareStatement("INSERT INTO user (username, email, password) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getEmail());
        stmt.setString(3, user.getPassword());
        result = stmt.executeUpdate();
        if (result > 0) {
          try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
              user.setId_user(generatedKeys.getInt(1));
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

  @Override
  public int update(int ID, User user) {
    int count = 0;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (Exception e) {
    }

    try (Connection con = getConnection("jdbc:mysql://localhost:3306/hobbyhandwerker", "adm_user", "the_password")) {
      String tableSql = "UPDATE user SET username = ?, email = ?, password = ? WHERE ID_user like ?;";
      try (PreparedStatement pstmt = con.prepareStatement(tableSql)) {
        System.out.println(user.getUsername() + "2");
        pstmt.setString(1, user.getUsername());
        pstmt.setString(2, user.getEmail());
        pstmt.setString(3, user.getPassword());
        pstmt.setInt(4, ID);
        System.out.println(pstmt);
        pstmt.execute();
        count = pstmt.getUpdateCount();
      } catch (Exception e) {
        e.printStackTrace();
        con.close();
      }
      return count;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return count;
  }
}
