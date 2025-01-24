package org.example.backend.models;

public class User {
  private int idUser;
  private String username;
  private String email;
  private String password;

  public User() {
  }

  public User(int idUser, String password, String username, String email) {
    this.idUser = idUser;
    this.password = password;
    this.username = username;
    this.email = email;
  }

  public int getIdUser() {
    return idUser;
  }

  public void setIdUser(int idUser) {
    this.idUser = idUser;
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
