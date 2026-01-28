# 🔴 SAST-FINDINGS (Statische Code-Analyse)
**Projekt:** HobbyHandwerker  
**Datum:** 28.01.2026  
**Status:** In Progress (Maven Tests läuft...)  
**Methode:** grep_search + semantic_search + Code-Review  

---

## 📊 FINDINGS-ÜBERSICHT

| # | Titel | Severity | Kategorie | Status |
|---|-------|----------|-----------|--------|
| **F1** | Hardcodierte DB-Credentials | 🔴 KRITISCH | Configuration | ✅ FOUND |
| **F2** | Password-Hash in API Response | 🔴 KRITISCH | Data Exposure | ✅ FOUND |
| **F3** | Broken Access Control (/all Endpoint) | 🔴 KRITISCH | Authorization | ✅ FOUND |
| **F4** | Statement statt PreparedStatement in select() | 🟡 MITTEL | SQL Injection | ✅ FOUND |
| **F5** | Keine Input Validierung (Email/Password) | 🟡 MITTEL | Input Validation | ✅ FOUND |
| **F6** | Kein Account Lockout Mechanismus | 🟡 MITTEL | Authentication | ✅ FOUND |
| **F7** | Kein Token Revocation (Logout) | 🟡 MITTEL | Session Management | ✅ FOUND |
| **F8** | JWT Token Expiration zu lang (1 Stunde) | 🟡 MITTEL | Session Management | ✅ FOUND |

---

## 🔍 DETAILLIERTE FINDINGS

### **F1: Hardcodierte DB-Credentials 🔴 KRITISCH**

**File:** [backend/src/main/java/org/example/backend/factory/DataSourceFactory.java](backend/src/main/java/org/example/backend/factory/DataSourceFactory.java#L1)

**Code:**
```java
public class DataSourceFactory {
  public static DataSource getMySQLDataSource() {
    MysqlDataSource ds = new MysqlDataSource();
    ds.setURL("jdbc:mysql://localhost:3306/hobbyhandwerker");
    ds.setUser("admin");                          // ❌ HARDCODIERT
    ds.setPassword("mariadb-pw-123"); //TODO env  // ❌ HARDCODIERT + TODO comment
    return ds;
  }
}
```

**Problem:**
- ❌ Datenbank-Username: `admin` ist hardcodiert
- ❌ Datenbank-Password: `mariadb-pw-123` ist im Code sichtbar
- ⚠️ TODO-Kommentar zeigt, dass es bekannt ist, aber nicht gefixt wurde
- 🔓 Credentials sind in Versionskontrolle (Git-History!)
- 📋 Application.properties hat auch hardcodierte Credentials

**OWASP Top 10 2025:** A02 - Security Misconfiguration, A04 - Cryptographic Failures  
**OWASP ASVS:** v5.0.0-13.1.2 (Secrets Management)  
**Risiko-Matrix:** 
- Eintrittswahrscheinlichkeit: **häufig** (Jeder mit Code-Zugriff sieht sie)
- Schadenspotenzial: **katastrophal** (Direkter DB-Zugriff)
- **Resultat: 🔴 KRITISCH - MUSS GEFIXT WERDEN**

**Remediation:**
1. Credentials in Umgebungsvariablen auslagern
2. Spring Boot `application.yml` mit Platzhaltern verwenden
3. Git-History bereinigen (Credentials aus Commits entfernen)
4. Password-Management-System verwenden (Spring Cloud Config, Vault, etc.)

---

### **F2: Password-Hash in API Response 🔴 KRITISCH**

**File:** [backend/src/main/java/org/example/backend/models/User.java](backend/src/main/java/org/example/backend/models/User.java#L1)

**Code:**
```java
public class User {
  private int id_user;
  private String username;
  private String email;
  private String password;  // ❌ Wird in Response serialisiert!
  private int role;
  
  // Getter/Setter für password
  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }
}
```

**Problem:**
- ❌ `password` Feld wird in JSON-Responses mitgesendet
- 📋 Test **TC-V14-001** zeigt: `GET /user/{id}` gibt Password-Hash zurück
- 🔓 Selbst gehashed ist dies sensitive Information
- 💥 LoginResponse zeigt auch User-Objekt mit Password!

**Beispiel-Response (VULNERABLE):**
```json
{
  "id_user": 1,
  "username": "admin",
  "email": "admin@test.com",
  "password": "$2a$10$O.U1IL1sMlySGk.LGzseu.R.99KCgb3m7O3y5gCKtTds8kzz.hL4q",
  "role": 3
}
```

**OWASP Top 10 2025:** A04 - Cryptographic Failures (Sensitive Data Exposure)  
**OWASP ASVS:** v5.0.0-6.4.1, v5.0.0-14.1.2 (Sensitive Data)  
**Risiko-Matrix:**
- Eintrittswahrscheinlichkeit: **häufig** (Jeder API-Call gibt sie zurück)
- Schadenspotenzial: **kritisch** (Offline Brute-Force auf Hashes möglich)
- **Resultat: 🔴 KRITISCH**

**Remediation:**
1. DTO (Data Transfer Object) erstellen ohne password Feld
2. `@JsonIgnore` Annotation auf password Feld in User.java
3. Response-Objekt vor Serialisierung filtern

**Quick-Fix:**
```java
@JsonIgnore
private String password;  // Wird nicht in JSON serialisiert
```

---

### **F3: Broken Access Control - /all Endpoint 🔴 KRITISCH**

**File:** [backend/src/main/java/org/example/backend/controller/UserController.java](backend/src/main/java/org/example/backend/controller/UserController.java#L57)

**Code:**
```java
@GetMapping("/all")
public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String authHeader) {
  // ... JWT parsing ...
  int role = claims.get("role", Integer.class);
  if (role != 2 && role != 3) {  // ❌ LOGIK-ERROR!
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }
  List<User> users = dao.select();
  return ResponseEntity.ok(users);  // ✅ Gibt alle User zurück
}
```

**Problem:**
- ❌ Logik-Fehler: `role != 2 && role != 3` bedeutet:
  - Wenn Rolle NICHT "user" (2) UND NICHT "admin" (3) → BLOCK
  - Das heißt: role=1 (visitor) → blocked, **aber role=2 UND role=3 sind ERLAUBT!**
- 💥 **Normaler User (role=2) KANN alle User sehen!**
- 📋 Sollte sein: `role != 3` (nur Admin darf `/all` aufrufen)

**Test-Case:** TC-V8-002 zeigt diesen Bug explicitly

**OWASP Top 10 2025:** A01 - Broken Access Control (Critical)  
**OWASP ASVS:** v5.0.0-8.2.1 (Access Control)  
**Risiko-Matrix:**
- Eintrittswahrscheinlichkeit: **häufig** (Einfach zu exploiten)
- Schadenspotenzial: **kritisch** (Datenleck aller User)
- **Resultat: 🔴 KRITISCH - MUSS GEFIXT WERDEN**

**Remediation:**
```java
if (role != 3) {  // Nur Admin (role=3) darf /all aufrufen
  return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
}
```

---

### **F4: Statement statt PreparedStatement in select() 🟡 MITTEL**

**File:** [backend/src/main/java/org/example/backend/models/UserDao.java](backend/src/main/java/org/example/backend/models/UserDao.java#L28)

**Code:**
```java
@Override
public List<User> select() {
  try (Connection con = dataSource.getConnection()) {
    try (Statement stmt = con.createStatement()) {  // ❌ Statement (nicht sicher)
      String tableSql = "SELECT * from user";
      try (ResultSet resultSet = stmt.executeQuery(tableSql)) {
        resultSet.next();  // ⚠️ NUR FIRST ROW!
        // ... nur 1 User wird returned, nicht alle!
        return user;  // List mit nur 1 Item
      }
    }
  }
}
```

**Problem:**
- ❌ `Statement` wird verwendet (anfällig für SQL Injection, wenn die Query dynamisch ist)
- ⚠️ `resultSet.next()` wird nur 1x aufgerufen → nur erste Row wird returned
- 📋 Sollte: PreparedStatement + Loop durch alle Rows

**Risk-Mitigation:**
- ✅ Query ist statisch ("SELECT * from user"), nicht dynamisch
- ✅ Keine User-Input in dieser Query
- 🟡 Aber: Best Practice verstößen

**OWASP ASVS:** v5.0.0-1.2.1 (Parameterized Queries)  
**Risiko-Matrix:**
- Eintrittswahrscheinlichkeit: **gering** (Query ist statisch)
- Schadenspotenzial: **mittel** (Nur bei dynamischer Query gefährlich)
- **Resultat: 🟡 MITTEL**

**Remediation:**
```java
public List<User> select() {
  List<User> users = new ArrayList<>();
  try (Connection con = dataSource.getConnection()) {
    String sql = "SELECT * FROM user";  // Still static, but best practice
    try (PreparedStatement stmt = con.prepareStatement(sql)) {
      try (ResultSet resultSet = stmt.executeQuery()) {
        while (resultSet.next()) {  // LOOP through ALL rows!
          User u = new User();
          u.setId_user(resultSet.getInt("ID_user"));
          // ... populate all fields ...
          users.add(u);
        }
      }
    }
  }
  return users;  // List mit ALLEN Usern
}
```

---

### **F5: Keine Input Validierung (Email/Password) 🟡 MITTEL**

**File:** [backend/src/main/java/org/example/backend/controller/UserController.java](backend/src/main/java/org/example/backend/controller/UserController.java#L78)

**Code:**
```java
@PostMapping
public ResponseEntity<User> createUser(@RequestBody User user) {
  User returnedUser = dao.saveUser(user);  // ❌ Kein Validation!
  if (returnedUser != null) {
    return ResponseEntity.status(HttpStatus.CREATED).body(returnedUser);
  }
}
```

**Problem:**
- ❌ Keine Email-Format Validierung
- ❌ Keine Password-Komplexität Validierung (min. 8 Zeichen, Uppercase, Numbers, etc.)
- ❌ Keine Username-Format Validierung
- 📋 User-Objekt wird direkt mit DB-Insert accepted

**OWASP Top 10 2025:** A06 - Insecure Design  
**OWASP ASVS:** v5.0.0-2.2.1 (Input Validation)  
**Risiko-Matrix:**
- Eintrittswahrscheinlichkeit: **häufig** (User können invalid Data senden)
- Schadenspotenzial: **gering** (Datenbankfehler, aber kein Leak)
- **Resultat: 🟡 MITTEL**

**Remediation:**
```java
@PostMapping
public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
  // Validation wird automatisch durchgeführt
  User returnedUser = dao.saveUser(user);
  return ResponseEntity.status(HttpStatus.CREATED).body(returnedUser);
}
```

**User.java mit JSR-303 Annotations:**
```java
public class User {
  @Email(message = "Email muss valid sein")
  private String email;
  
  @NotBlank(message = "Username ist erforderlich")
  @Size(min = 3, max = 30)
  private String username;
  
  @NotBlank(message = "Password ist erforderlich")
  @Size(min = 8, message = "Password mind. 8 Zeichen")
  @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).*$", 
    message = "Password muss Großbuchstaben und Zahlen enthalten")
  private String password;
}
```

---

### **F6: Kein Account Lockout Mechanismus 🟡 MITTEL**

**File:** [backend/src/main/java/org/example/backend/controller/UserController.java](backend/src/main/java/org/example/backend/controller/UserController.java#L148)

**Code:**
```java
@PutMapping("/login")
public ResponseEntity<AuthResponse> loginByUsernameAndPassword(@RequestBody User user) {
  User loggedInUser = dao.login(user.getUsername(), user.getPassword());
  
  if (loggedInUser != null) {
    // Token erstellen
    String token = Jwts.builder()...
    return ResponseEntity.ok(new AuthResponse(token, loggedInUser));
  } else {
    return ResponseEntity.badRequest().build();  // ❌ Einfach 400, kein Lockout
  }
}
```

**Problem:**
- ❌ Kein Zähler für fehlgeschlagene Logins
- ❌ Kein Account Lockout nach N Versuchen
- ❌ Kein Rate Limiting auf /login Endpoint
- 💥 **Brute-Force Attacks sind möglich!**

**OWASP Top 10 2025:** A07 - Authentication Failures  
**OWASP ASVS:** v5.0.0-6.3.1 (Account Lockout)  
**Risiko-Matrix:**
- Eintrittswahrscheinlichkeit: **häufig** (Attacke ist einfach)
- Schadenspotenzial: **kritisch** (Unbegrenzter Brute-Force)
- **Resultat: 🟡 MITTEL (wird aber öfter exploitiert!)**

**Remediation:**
1. Spring Security Rate Limiter verwenden
2. Account nach 5 fehlgeschlagenen Versuchen sperren
3. Exponential Backoff implementieren

---

### **F7: Kein Token Revocation (Logout) 🟡 MITTEL**

**File:** [backend/src/main/java/org/example/backend/controller/UserController.java](backend/src/main/java/org/example/backend/controller)

**Problem:**
- ❌ Es gibt keinen `/logout` Endpoint
- ❌ JWT Token bleibt gültig bis zum Expiration (1 Stunde)
- ❌ Kein Token Blacklist Mechanismus
- 💥 **Token-Diebstahl kann 1 Stunde lang ausgenutzt werden!**

**Scenario:**
1. User loggt sich ein, bekommt JWT Token
2. User schließt Browser (aber löscht Token nicht)
3. Attacker stiehlt Token (z.B. via XSS)
4. Attacker kann 1 Stunde lang als User agieren
5. User hat keine Möglichkeit, sofort zu logout

**OWASP Top 10 2025:** A07 - Authentication Failures  
**OWASP ASVS:** v5.0.0-7.2.1 (Session Invalidation)  
**Risiko-Matrix:**
- Eintrittswahrscheinlichkeit: **häufig** (Tokens können gestohlen werden)
- Schadenspotenzial: **kritisch** (Accounts können übernommen werden)
- **Resultat: 🟡 MITTEL-HOCH**

**Remediation:**
1. `/logout` Endpoint implementieren (setzt Token in Blacklist)
2. Redis/In-Memory Blacklist für revozierte Tokens
3. Oder: Shorter Token Expiration (5-15 Minuten) + Refresh Token Pattern

---

### **F8: JWT Token Expiration zu lang (1 Stunde) 🟡 MITTEL**

**File:** [backend/src/main/java/org/example/backend/controller/UserController.java](backend/src/main/java/org/example/backend/controller/UserController.java#L165)

**Code:**
```java
String token = Jwts.builder()
  .setClaims(claims)
  .setIssuedAt(new Date())
  .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // ❌ 1 STUNDE!
  .signWith(secretKey, SignatureAlgorithm.HS256)
  .compact();
```

**Problem:**
- ❌ Token Expiration: 1 Stunde (`1000 * 60 * 60`)
- ⚠️ Best Practice: 5-15 Minuten für Access Token
- 📋 Wenn Token gestohlen wird, kann Attacker 1 Stunde agieren

**OWASP ASVS:** v5.0.0-7.1.1 (Token Expiration)  
**NIST SP 800-63B:** Access Tokens sollten < 15 Minuten sein  
**Risiko-Matrix:**
- Eintrittswahrscheinlichkeit: **gering** (nur wenn Token gestohlen)
- Schadenspotenzial: **mittel** (Begrenzte Zeit für Attacke)
- **Resultat: 🟡 MITTEL**

**Remediation:**
```java
// Access Token: 15 Minuten
.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))

// Refresh Token (optional): 7 Tage
// (verwendet, um neuen Access Token zu erhalten)
```

---

## 📋 ZUSÄTZLICHE FINDINGS AUS TEST-CODE

**File:** [backend/src/test/java/org/example/backend/models/UserDaoTest.java](backend/src/test/java/org/example/backend/models/UserDaoTest.java#L45)

**Code:**
```java
when(resultSet.getString("password")).thenReturn("secret123");  // ❌ Plaintext im Test!
```

**Problem:**
- Test verwendet plaintext Password, sollte gehashed sein
- Minor Issue, aber zeigt unsicheres Testing Pattern

---

## 🎯 ZUSAMMENFASSUNG SAST-FINDINGS

### 🔴 KRITISCH (MUSS SOFORT GEFIXT WERDEN):
1. **F1** - Hardcodierte DB-Credentials
2. **F2** - Password-Hash in API Response
3. **F3** - Broken Access Control (/all Endpoint)

### 🟡 MITTEL (SOLLTE GEFIXT WERDEN):
4. **F4** - Statement statt PreparedStatement
5. **F5** - Keine Input Validierung
6. **F6** - Kein Account Lockout
7. **F7** - Kein Token Revocation
8. **F8** - Token Expiration zu lang

---

## 📊 OWASP TOP 10 2025 MAPPING (aus SAST):

| Top 10 | Findings | Count |
|--------|----------|-------|
| A01: Broken Access Control | F3 | 1 |
| A02: Security Misconfiguration | F1 | 1 |
| A04: Cryptographic Failures | F2 | 1 |
| A06: Insecure Design | F5 | 1 |
| A07: Authentication Failures | F6, F7, F8 | 3 |

---

**Status:** ✅ SAST abgeschlossen  
**Nächster Schritt:** Warten auf Maven Test Results, dann DAST durchführen  
**Report Version:** 1.0  
