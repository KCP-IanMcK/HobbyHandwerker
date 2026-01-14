package org.example.backend.models;

import java.util.List;
import java.util.Optional;

public interface IUserDao {
  List<User> select();
  Optional<User> select(int ID);
  Optional<User> saveUser(User user);
  int update(int ID, User user);
  Optional<User> login(String username, String passwordSha);
}
