package org.example.backend.models;

import org.example.backend.service.PasswordService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class UserDao implements IUserDao {

  private final DataSource dataSource;

  private final PasswordService passwordService = new PasswordService();

  public UserDao(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<User> select() {
    String sql = "SELECT * FROM user";

    List<User> users = new ArrayList<>();

    try (Connection con = dataSource.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

      while (rs.next()) {
        User u = new User();
        u.setId_user(rs.getInt("ID_user"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setRole(rs.getInt("FS_Role"));

        users.add(u);
      }

      return users;

    } catch (SQLException e) {
      throw new RuntimeException("Fehler beim Laden der User", e);
    }
  }

  @Override
  public Optional<User> select(int id) {

    String sql = "SELECT * FROM user WHERE ID_user = ?";

    try (Connection con = dataSource.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

      ps.setInt(1, id);

      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return Optional.empty();
        }

        User user = new User();
        user.setId_user(rs.getInt("ID_user"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getInt("FS_Role"));

        return Optional.of(user);
      }

    } catch (SQLException e) {
      throw new RuntimeException("Fehler beim Laden des Users", e);
    }
  }

  @Override
  public Optional<User> saveUser(User user) {

    user.setPassword(passwordService.hashPassword(user.getPassword()));

    String sql = "INSERT INTO user (username, email, password, FS_Role) VALUES (?, ?, ?, ?)";

    try (Connection con = dataSource.getConnection();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      ps.setString(1, user.getUsername());
      ps.setString(2, user.getEmail());
      ps.setString(3, user.getPassword());
      ps.setInt(4, 2); // role USER

      int affectedRows = ps.executeUpdate();
      if (affectedRows == 0) {
        return Optional.empty();
      }

      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) {
          user.setId_user(keys.getInt(1));
        }
      }

      return Optional.of(user);

    } catch (SQLException e) {
      throw new RuntimeException("User konnte nicht gespeichert werden", e);
    }
  }

  @Override
  public int update(int id, User user) {

    if (user.getPassword() != null) {
      user.setPassword(passwordService.hashPassword(user.getPassword()));
    }

    Map<String, Object> fields = new LinkedHashMap<>();

    if (user.getUsername() != null) {
      fields.put("username", user.getUsername());
    }
    if (user.getEmail() != null) {
      fields.put("email", user.getEmail());
    }
    if (user.getPassword() != null) {
      fields.put("password", user.getPassword());
    }

    if (fields.isEmpty()) {
      return 0;
    }

    String sql = "UPDATE user SET " +
      fields.keySet().stream()
        .map(k -> k + " = ?")
        .collect(Collectors.joining(", "))
      + " WHERE ID_user = ?";

    try (Connection con = dataSource.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

      int index = 1;
      for (Object value : fields.values()) {
        ps.setObject(index++, value);
      }
      ps.setInt(index, id);

      return ps.executeUpdate();

    } catch (SQLException e) {
      throw new RuntimeException("Update fehlgeschlagen", e);
    }
  }


  @Override
  public Optional<User> login(String username, String passwordSha) {

    String sql = "SELECT * FROM user WHERE username = ?";

    try (Connection con = dataSource.getConnection();
         PreparedStatement stmt = con.prepareStatement(sql)) {

      stmt.setString(1, username);

      try (ResultSet rs = stmt.executeQuery()) {

        if (!rs.next()) {
          return Optional.empty();
        }

        if (!passwordService.verifyPassword(passwordSha, rs.getString("password"))) {
          return Optional.empty();
        }

        User user = new User();
        user.setId_user(rs.getInt("ID_user"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getInt("FS_Role"));

        return Optional.of(user);
      }

    } catch (SQLException e) {
      throw new RuntimeException("Login fehlgeschlagen", e);
    }
  }
}
