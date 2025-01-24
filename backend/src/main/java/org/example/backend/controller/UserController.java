package org.example.backend.controller;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.backend.models.IUserDao;
import org.example.backend.models.User;
import org.example.backend.models.UserDao;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

  private IUserDao dao = new UserDao();

  public void setDao(IUserDao dao) {
    this.dao = dao;
  }

  @Produces(MediaType.APPLICATION_JSON)
  @GetMapping("/all")
  public ResponseEntity getAllUsers() {
    List<User> user = new ArrayList<>();

    user.addAll(dao.select());
    if (user.size() > 0) {
      System.out.println(">0");
      return ResponseEntity.ok(user);
    } else {
      System.out.println("<0");
      return ResponseEntity.notFound().build();
    }
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PostMapping
  public ResponseEntity createUser(@RequestBody User user) {
    User returnedUser = dao.saveUser(user);
    if (returnedUser != null) {
      return ResponseEntity.status(HttpStatus.CREATED).body(returnedUser);
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
