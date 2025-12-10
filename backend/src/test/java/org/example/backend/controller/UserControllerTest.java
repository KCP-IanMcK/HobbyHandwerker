package org.example.backend.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.checkerframework.checker.units.qual.A;
import org.example.backend.models.AuthResponse;
import org.example.backend.models.User;
import org.example.backend.models.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  UserDao userDao;

  @InjectMocks
  UserController userController;

  List<User> users = new ArrayList<>();
  User user1;
  String adminToken;

  @BeforeEach
  void setUp() {
    user1 = new User();
    user1.setId_user(1);

    User user2 = new User();
    user2.setId_user(2);

    users.add(user1);
    users.add(user2);

    String secret = "mySuperSecretKey12345678901234567890";
    userController.jwtSecret = secret;
    Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    // Claims erzeugen
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", 1);
    claims.put("username", "admin");
    claims.put("role", 3);

    // Token erstellen
    adminToken = Jwts.builder()
      .setClaims(claims)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  @Test
  void setDao() {
    assertDoesNotThrow(() -> userController.setDao(userDao));
  }

  @Test
  void getAllUsers() {
    when(userDao.select()).thenReturn(users);
    ResponseEntity<List<User>> expectedResult = ResponseEntity.ok().body(users);

    var result = userController.getAllUsers(adminToken);

    assertEquals(expectedResult, result);
  }

  @Test
  void createUser() {
    when(userDao.saveUser(user1)).thenReturn(user1);
    ResponseEntity<User> expectedResult = ResponseEntity.status(201).body(user1);

    var result = userController.createUser(user1);

    assertEquals(expectedResult, result);
  }

  @Test
  void getUserByID() {
    when(userDao.select(1)).thenReturn(user1);
    ResponseEntity<User> expectedResult = ResponseEntity.ok().body(user1);

    var result = userController.getUserByID(1);

    assertEquals(expectedResult, result);
  }

  @Test
  void updateUserByID() {
    when(userDao.update(1, user1)).thenReturn(1);
    ResponseEntity<Integer> expectedResult = ResponseEntity.ok(1);

    var result = userController.updateUserByID(adminToken, user1, 1);

    assertEquals(expectedResult, result);
  }

  @Test
  void loginUser_success() {
    User user = new User();
    when(userDao.login("user1", "password")).thenReturn(user);
    AuthResponse authResponse = new AuthResponse(adminToken, user);
    ResponseEntity<AuthResponse> expectedResult = ResponseEntity.ok(authResponse);

    User userToLogin = new User();
    userToLogin.setUsername("user1");
    userToLogin.setPassword("password");

    var result = userController.loginByUsernameAndPassword(userToLogin);

    assertEquals(expectedResult.getBody().getUser(), result.getBody().getUser());
  }

  @Test
  void loginUser_fail() {
    when(userDao.login("user1", "password")).thenReturn(null);
    ResponseEntity<User> expectedResult = ResponseEntity.badRequest().build();

    User userToLogin = new User();
    userToLogin.setUsername("user1");
    userToLogin.setPassword("password");

    var result = userController.loginByUsernameAndPassword(userToLogin);

    assertEquals(expectedResult, result);
  }
}
