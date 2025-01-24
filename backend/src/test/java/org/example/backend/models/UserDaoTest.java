package org.example.backend.models;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

  @Test
  void select() {
    IUserDao dao = new UserDao();
    assertEquals(new User().getClass(), dao.select().getFirst().getClass());
  }

  @Test
  void selectUserByID() {
    IUserDao dao = new UserDao();
    int id = 1;
    assertEquals(id, dao.select(id).getId_user());
  }

  @Test
  void update() {
    IUserDao dao = new UserDao();
    int id = 1;
    User user = new User(1, "1234", "Hans", "hans@email.com");
    assertEquals(1, dao.update(id, user));
  }

  @Test
  void insert() {
    IUserDao dao = new UserDao();
    User user = new User(2, "1234", "Anna", "anna@email.com");
    assertEquals(user.getClass(), dao.saveUser(user).getClass());
  }
}
