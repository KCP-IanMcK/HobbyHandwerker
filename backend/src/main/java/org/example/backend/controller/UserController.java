package org.example.backend.controller;

import org.example.backend.models.IUserDao;
import org.example.backend.models.User;
import org.example.backend.models.UserDao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

  private IUserDao dao = new UserDao();

  public void setDao(IUserDao dao) {
    this.dao = dao;
  }

  @GetMapping("/all")
  public ResponseEntity<List<User>> sayHello() {
    List<User> user = new ArrayList<>();

//    user.addAll(dao.select());
    if (user.size() > 0) {
      return ResponseEntity.ok(user);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
