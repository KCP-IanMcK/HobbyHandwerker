package org.example.backend.models;

import java.util.List;

public interface IUserDao {
  public List<User> select();
  public User select(int ID);
  public int update(int ID, User user);
}
