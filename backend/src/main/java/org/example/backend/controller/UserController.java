package org.example.backend.controller;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.example.backend.factory.DataSourceFactory;
import org.example.backend.models.IUserDao;
import org.example.backend.models.User;
import org.example.backend.models.UserDao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
  private final DataSource dataSource = DataSourceFactory.getMySQLDataSource();
  private IUserDao dao = new UserDao(dataSource);

  public void setDao(IUserDao dao) {
    this.dao = dao;
  }

  @Produces(MediaType.APPLICATION_JSON)
  @GetMapping("/all")
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> user = new ArrayList<>();

    user.addAll(dao.select());
    if (!user.isEmpty()) {
      return ResponseEntity.ok(user);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody User user) {
    User returnedUser = dao.saveUser(user);
    if (returnedUser != null) {
      return ResponseEntity.status(HttpStatus.CREATED).body(returnedUser);
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Produces(MediaType.APPLICATION_JSON)
  @GetMapping("/{user_id}")
  public ResponseEntity<User> getUserByID(@PathVariable("user_id") int user_id) {
    User user = dao.select(user_id);

    if (user != null) {
      return ResponseEntity.ok(user);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{user_id}")
  public ResponseEntity<Integer> updateUserByID(@RequestBody User user, @PathVariable("user_id") int user_id) {
    int count = dao.update(user_id, user);
    if(count == 1) {
      return ResponseEntity.ok(count);
    }
    else {
      return ResponseEntity.badRequest().build();
    }
  }
}
