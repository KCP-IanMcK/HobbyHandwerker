package org.example.backend.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
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
  String jwtSecret;

  public void setDao(IUserDao dao) {
    this.dao = dao;
  }

  @Produces(MediaType.APPLICATION_JSON)
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
    if(role != 2 && role != 3) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

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
  public ResponseEntity<Integer> updateUserByID(@RequestHeader("Authorization") String authHeader, @RequestBody User user, @PathVariable("user_id") int user_id) {
    String token = authHeader.replace("Bearer ", "");
    Key secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    Claims claims = Jwts.parserBuilder()
      .setSigningKey(secretKey)
      .build()
      .parseClaimsJws(token)
      .getBody();

    int role = claims.get("role", Integer.class);
    int userId = claims.get("userId", Integer.class);

    if(!(role == 2 && userId == user.getId_user()) && role != 3) {
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
    User logedInUser = dao.login(user.getUsername(), user.getPassword());

    if (logedInUser != null) {
      Map<String, Object> claims = new HashMap<>();
      claims.put("userId", user.getId_user());
      claims.put("username", user.getUsername());
      claims.put("role", user.getRole());


      Key secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

      String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
      return ResponseEntity.ok(new AuthResponse(token, logedInUser));
    } else {
      return ResponseEntity.badRequest().build();
    }
  }
}
