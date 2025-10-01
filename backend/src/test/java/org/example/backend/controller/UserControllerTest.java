package org.example.backend.controller;

import org.example.backend.models.User;
import org.example.backend.models.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

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

  @BeforeEach
  void setUp() {
    user1 = new User();
    user1.setId_user(1);

    User user2 = new User();
    user2.setId_user(2);

    users.add(user1);
    users.add(user2);
  }

  @Test
  void setDao() {
    assertDoesNotThrow(() -> userController.setDao(userDao));
  }

  @Test
  void getAllUsers() {
    when(userDao.select()).thenReturn(users);
    ResponseEntity<List<User>> expectedResult = ResponseEntity.ok().body(users);

    var result = userController.getAllUsers();

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

    var result = userController.updateUserByID(user1, 1);

    assertEquals(expectedResult, result);
  }
}
