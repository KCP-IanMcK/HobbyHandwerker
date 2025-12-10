package org.example.backend.models;

import java.util.List;

public interface IUserDao {
  public List<User> select();
  public User select(int ID);
  public User saveUser(User user);
  public int update(int ID, User user);
  public User login(String username, String passwordSha);
}
