package org.example.backend.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.backend.factory.DataSourceFactory;
import org.example.backend.models.AuthResponse;
import org.example.backend.models.IUserDao;
import org.example.backend.models.User;
import org.example.backend.models.UserDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/user")
public class UserController {

  private final DataSource dataSource = DataSourceFactory.getMySQLDataSource();
  private IUserDao dao = new UserDao(dataSource);

  @Value("${jwt.secret}")
  private String jwtSecret;

  public void setDao(IUserDao dao) {
    this.dao = dao;
  }

  @GetMapping("/all")
  public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    Key secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    Claims claims = Jwts.parserBuilder()
      .setSigningKey(secretKey)
      .build()
      .parseClaimsJws(token)
      .getBody();
    int role = claims.get("role", Integer.class);
    if (role != 2 && role != 3) { // user oder admin
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    List<User> users = dao.select();
    if (!users.isEmpty()) {
      return ResponseEntity.ok(users);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody User user) {
    User returnedUser = dao.saveUser(user);
    if (returnedUser != null) {
      return ResponseEntity.status(HttpStatus.CREATED).body(returnedUser);
    } else {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

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
  public ResponseEntity<Integer> updateUserByID(@RequestHeader("Authorization") String authHeader,
                                                @RequestBody User user,
                                                @PathVariable("user_id") int user_id) {
    String token = authHeader.replace("Bearer ", "");
    Key secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    Claims claims = Jwts.parserBuilder()
      .setSigningKey(secretKey)
      .build()
      .parseClaimsJws(token)
      .getBody();

    int role = claims.get("role", Integer.class);
    int userId = claims.get("userId", Integer.class);

    if (!(role == 2 && userId == user.getId_user()) && role != 3) { // user oder admin
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    int count = dao.update(user_id, user);
    if (count == 1) {
      return ResponseEntity.ok(count);
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/login")
  public ResponseEntity<AuthResponse> loginByUsernameAndPassword(@RequestBody User user) {
    User loggedInUser = dao.login(user.getUsername(), user.getPassword());

    if (loggedInUser != null) {
      Map<String, Object> claims = new HashMap<>();
      claims.put("userId", loggedInUser.getId_user());
      claims.put("username", loggedInUser.getUsername());
      claims.put("role", loggedInUser.getRole());

      Key secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

      String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
      return ResponseEntity.ok(new AuthResponse(token, loggedInUser));
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  @PostMapping("/login")
  public ResponseEntity<User> login(@RequestBody User loginUser) {
    User user = dao.login(loginUser.getUsername(), loginUser.getPassword());

    if (user != null) {
      return ResponseEntity.ok(user);
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }
}
