package org.example.backend.models;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserDaoTest {
  IUserDao dao;
  DataSource mockDataSource = mock(DataSource.class);

  @BeforeEach
  void setUp() throws Exception {
    Connection mockConnection = mock(Connection.class);
    Statement mockStatement = mock(Statement.class);
    ResultSet mockResultSet = mock(ResultSet.class);
    PreparedStatement mockPrepStatement = mock(PreparedStatement.class);
    ResultSet resultSet = mock(ResultSet.class);


    when(mockDataSource.getConnection()).thenReturn(mockConnection);
    when(mockConnection.createStatement()).thenReturn(mockStatement);
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPrepStatement);
    when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPrepStatement);
    when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

    when(mockResultSet.next()).thenReturn(true, false);
    when(mockResultSet.getInt("ID_user")).thenReturn(1);
    when(mockResultSet.getString("username")).thenReturn("testuser");
    when(mockResultSet.getString("email")).thenReturn("test@example.com");
    when(mockResultSet.getString("password")).thenReturn("secret");
    when(mockPrepStatement.executeQuery()).thenReturn(mockResultSet);
    when(mockPrepStatement.getUpdateCount()).thenReturn(1);
    when(mockPrepStatement.executeUpdate()).thenReturn(1);
    when(mockPrepStatement.getGeneratedKeys()).thenReturn(mockResultSet);

    when(resultSet.next()).thenReturn(true);
    when(resultSet.getInt("ID_user")).thenReturn(1);
    when(resultSet.getString("username")).thenReturn("johndoe");
    when(resultSet.getString("email")).thenReturn("john@example.com");
    when(resultSet.getString("password")).thenReturn("secret123");
    dao = new UserDao(mockDataSource);
  }

  @Test
  void select() {
    assertEquals(User.class, dao.select().getFirst().getClass());
  }

  @Test
  void selectUserByID() {
    int id = 1;
    assertEquals(id, dao.select(id).getId_user());
  }

  @Test
  void update() {
    int id = 1;
    User user = new User(1, 2, "1234", "hans@email.com", "Hans");
    assertEquals(1, dao.update(id, user));
  }

  @Test
  void insert() {
    User user = new User(2, 2, "1234", "anna@email.com", "Anna");

    User result = dao.saveUser(user);

    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(result.getId_user()).isEqualTo(user.getId_user());
    softly.assertThat(result.getPassword()).isEqualTo(user.getPassword());
    softly.assertThat(result.getUsername()).isEqualTo(user.getUsername());
    softly.assertThat(result.getEmail()).isEqualTo(user.getEmail());
  }
}
