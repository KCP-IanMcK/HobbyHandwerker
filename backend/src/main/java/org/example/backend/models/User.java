package org.example.backend.models;

public class User {
  private int id_user;
  private String username;
  private String email;
  private String password;
  private int role;

  public User() {
  }

  public User(int id_user, int role, String password, String email, String username) {
    this.id_user = id_user;
    this.role = role;
    this.password = password;
    this.email = email;
    this.username = username;
  }

  public int getRole() {
    return role;
  }

  public void setRole(int role) {
    this.role = role;
  }

  public int getId_user() {
    return id_user;
  }

  public void setId_user(int id_user) {
    this.id_user = id_user;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
