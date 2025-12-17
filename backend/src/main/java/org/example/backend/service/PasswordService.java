package org.example.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordService {

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public String hashPassword(String plainPassword) {
    return passwordEncoder.encode(plainPassword);
  }

  public boolean verifyPassword(String plainPassword, String hashedPassword) {
    return passwordEncoder.matches(plainPassword, hashedPassword);
  }
}
