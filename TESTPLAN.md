# 🔒 SECURITY TESTPLAN - HobbyHandwerker App
## Phase 2: Sicherheitstests durch Tester-Team

**Projekt:** HobbyHandwerker  
**Version:** 1.0  
**Datum:** 28. Januar 2026  
**Tester:** Security Specialists  
**Standard:** OWASP ASVS 5.0.0 + OWASP Top 10 2025  

---

## 📊 TESTPLAN-ÜBERSICHT

Dieser Testplan orientiert sich an:
- **OWASP ASVS 5.0.0** für systematische Testabdeckung
- **OWASP Top 10 2025** für kritische Risiken
- **Risikomanagement-Matrix** für Severity-Bewertung

**Ziel:** > 75% Testabdeckung der Sicherheitsaspekte (angestrebt: 4/4 Punkte)

---

## 🎯 TESTBARE KAPITEL (OWASP ASVS 5.0.0)

Folgende ASVS-Kapitel sind für diese Web-App relevant:

| Kapitel | Name | Relevanz | Status |
|---------|------|----------|--------|
| **V1** | Encoding & Injection Prevention | 🔴 HOCH | ⏳ |
| **V2** | Input Validation & Business Logic | 🔴 HOCH | ⏳ |
| **V3** | Web Frontend Security | 🟡 MITTEL | ⏳ |
| **V6** | Authentication | 🔴 HOCH | ⏳ |
| **V7** | Session Management & JWT | 🔴 HOCH | ⏳ |
| **V8** | Authorization & Access Control | 🔴 HOCH | ⏳ |
| **V9** | Self-contained Tokens (JWT) | 🔴 HOCH | ⏳ |
| **V11** | Cryptography | 🔴 HOCH | ⏳ |
| **V12** | Secure Communication (TLS) | 🟡 MITTEL | ⏳ |
| **V13** | Configuration & Secrets | 🟡 MITTEL | ⏳ |
| **V14** | Data Protection & Privacy | 🟡 MITTEL | ⏳ |
| **V16** | Logging & Error Handling | 🟡 MITTEL | ⏳ |

---

## 📋 DETAILLIERTE TESTCASES

### **1. V1 - ENCODING & INJECTION PREVENTION** 🔴 KRITISCH

**OWASP Top 10 2025 Mapping:** A05 - Injection

#### TC-V1-001: SQL Injection via Username Login
**Risiko:** 🔴 KRITISCH (Eintrittswahrscheinlichkeit: häufig, Schaden: katastrophal)

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Versuchen, SQL Injection Payload im Login-Username-Feld einzuschleusen |
| **Vorbedingung** | App läuft, Login-Seite ist erreichbar |
| **Input** | `admin' OR '1'='1` |
| **Erwartet** | Login schlägt fehl, keine Authentifizierung ohne gültiges Passwort |
| **Methode** | Manual (Browser) + Postman |
| **ASVS Requirement** | v5.0.0-1.2.1 |

**Testschritte:**
1. Öffne Login-Formular im Browser
2. Gebe als Username: `admin' OR '1'='1` ein
3. Beliebiges Passwort eingeben
4. Login-Button klicken
5. Beobachte: Sollte rejected werden, nicht authentifiziert

**Erwartetes Resultat:** ✅ PreparedStatement blockiert die Injection (Code nutzt PreparedStatement)

---

#### TC-V1-002: SQL Injection via Email
**Risiko:** 🔴 KRITISCH

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | SQL Injection beim User erstellen (Email-Feld) |
| **Input** | `test@test.com'); DROP TABLE user; --` |
| **Erwartet** | User wird erstellt mit der Email, kein Table Drop |
| **Methode** | Postman |
| **ASVS Requirement** | v5.0.0-1.2.1 |

**cURL-Beispiel:**
```bash
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@test.com'\''); DROP TABLE user; --",
    "password": "Test123!"
  }'
```

**Erwartetes Resultat:** ✅ Email wird escaped, kein SQL-Fehler

---

#### TC-V1-003: SQL Injection in UserDao.select() - ❌ POTENZIELLES PROBLEM
**Risiko:** 🔴 KRITISCH

**Code-Analyse:**
```java
// UserDao.java - Zeile 28-30
String tableSql = "SELECT * from user";
try (Statement stmt = con.createStatement()) {
  try (ResultSet resultSet = stmt.executeQuery(tableSql)) {
    resultSet.next(); // ⚠️ NUR FIRST ROW RETURNED!
```

**Problem:** 
- ❌ Verwendet `Statement` statt `PreparedStatement` in `select()` Methode
- ⚠️ Nur eine Reihe wird returned (sollte alle sein)

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Überprüfe, ob select() Methode SQL-Injection-sicher ist |
| **Risiko** | Theoretisch vulnerabel, aber wird mit GET /all aufgerufen |
| **Erwartet** | PreparedStatement sollte verwendet werden |
| **ASVS Requirement** | v5.0.0-1.2.1 |

---

### **2. V2 - INPUT VALIDATION & BUSINESS LOGIC** 🔴 KRITISCH

**OWASP Top 10 2025 Mapping:** A05 - Injection, A06 - Insecure Design

#### TC-V2-001: Fehlende Email-Validierung
**Risiko:** 🟡 MITTEL (Eintrittswahrscheinlichkeit: häufig, Schaden: gering)

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Registriere User mit invalider Email-Adresse |
| **Input** | `"email": "not-a-valid-email"` |
| **Erwartet** | Validierungsfehler, User wird nicht erstellt |
| **Methode** | Postman |
| **ASVS Requirement** | v5.0.0-2.2.1 |

**cURL:**
```bash
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "not-a-valid-email",
    "password": "Test123!"
  }'
```

**Erwartetes Resultat:** ❌ User wird wahrscheinlich mit invalider Email erstellt (VULNERABILITY)

---

#### TC-V2-002: Fehlende Password-Komplexität-Validierung
**Risiko:** 🟡 MITTEL

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Registriere User mit schwachem Passwort |
| **Input Varianten** | `"password": "123"`, `"password": "a"`, `"password": ""` |
| **Erwartet** | Validierungsfehler für schwache Passwörter |
| **Methode** | Postman |
| **ASVS Requirement** | v5.0.0-2.3.1 |

**Erwartetes Resultat:** ❌ Schwache Passwörter werden akzeptiert (VULNERABILITY)

---

#### TC-V2-003: Null/Empty Input Handling
**Risiko:** 🟡 MITTEL

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Sende leere oder Null-Felder |
| **Input Varianten** | `{"username": null, "email": "", "password": "test"}` |
| **Erwartet** | Validierungsfehler für erforderliche Felder |
| **Methode** | Postman |
| **ASVS Requirement** | v5.0.0-2.1.1 |

**Erwartetes Resultat:** ❌ Null-Check fehlt möglicherweise (VULNERABILITY möglich)

---

### **3. V6 - AUTHENTICATION** 🔴 KRITISCH

**OWASP Top 10 2025 Mapping:** A07 - Authentication Failures

#### TC-V6-001: Fehlender Account Lockout nach fehlgeschlagenen Logins
**Risiko:** 🔴 KRITISCH (Eintrittswahrscheinlichkeit: häufig, Schaden: kritisch - Brute Force möglich)

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Versuche 10x mit falschen Passwörtern zu loggen |
| **Input** | Username: `admin`, Passwort: falsch (10x) |
| **Erwartet** | Nach 3-5 Versuchen: Account gesperrt / IP-Ban / Verzögerung |
| **Methode** | Postman / Automated Script |
| **ASVS Requirement** | v5.0.0-6.3.1 |

**Test-Script (Bash):**
```bash
for i in {1..10}; do
  curl -X PUT http://localhost:8080/user/login \
    -H "Content-Type: application/json" \
    -d '{"username": "admin", "password": "wrongpassword'$i'"}'
  echo "Attempt $i"
  sleep 0.5
done
```

**Erwartetes Resultat:** ❌ Kein Account Lockout implementiert (VULNERABILITY)

---

#### TC-V6-002: Passwort-Wiederholung möglich (kein Passwort-History)
**Risiko:** 🟡 MITTEL

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Ändere Passwort auf altes Passwort |
| **Vorbedingung** | Login mit User, Auth-Token erforderlich |
| **Input** | Neues Passwort = altes Passwort |
| **Erwartet** | Fehler: "Password already used in history" |
| **Methode** | Postman |
| **ASVS Requirement** | v5.0.0-6.2.4 |

**Erwartetes Resultat:** ❌ Keine Passwort-History Prüfung (VULNERABILITY)

---

#### TC-V6-003: Passwort in Response exponiert (User Lookup)
**Risiko:** 🔴 KRITISCH

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Hole User-Daten via GET /user/{user_id} |
| **Authorization** | Gültiger JWT Token (Admin) |
| **Erwartet** | Response enthält NICHT das Password-Hash |
| **Methode** | Postman |
| **ASVS Requirement** | v5.0.0-6.4.1 |

**cURL:**
```bash
curl -X GET http://localhost:8080/user/1 \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

**Response-Analyse:**
```json
{
  "id_user": 1,
  "username": "admin",
  "email": "admin@example.com",
  "password": "$2a$10$..." // ⚠️ PASSWORD-HASH SOLLTE NICHT HIER SEIN!
}
```

**Erwartetes Resultat:** ❌ Password wird in Response zurückgegeben (VULNERABILITY)

---

### **4. V7 - SESSION MANAGEMENT & JWT** 🔴 KRITISCH

**OWASP Top 10 2025 Mapping:** A07 - Authentication Failures

#### TC-V7-001: JWT Token Expiration Time
**Risiko:** 🟡 MITTEL (Eintrittswahrscheinlichkeit: häufig, Schaden: kritisch - Token Hijacking)

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Überprüfe JWT Token Expiration Zeit |
| **Methode** | JWT.io decoder, Manual |
| **Erwartet** | Token hat Expiration Zeit < 1 Stunde (empfohlen: 15 Min) |
| **ASVS Requirement** | v5.0.0-7.1.1 |

**Test-Durchführung:**
1. Login durchführen: `POST /user/login`
2. JWT Token aus Response extrahieren
3. Token auf https://jwt.io dekodieren
4. `exp` Claim überprüfen

**Erwartetes Resultat:**
```json
{
  "userId": 1,
  "username": "admin",
  "role": 3,
  "iat": 1706424000,  // Issued at
  "exp": 1706427600   // Expires in (1 hour = 3600 seconds)
}
```

**Problem:** Token hat 1 Stunde Gültigkeit - sollte kürzer sein!

---

#### TC-V7-002: JWT Token Revocation nicht möglich
**Risiko:** 🔴 KRITISCH

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Versuche, JWT Token zu revozieren (Logout) |
| **Vorbedingung** | User eingeloggt mit JWT Token |
| **Methode** | Suche nach Logout-Endpoint |
| **Erwartet** | Logout-Endpoint existiert und invalidiert Token |
| **ASVS Requirement** | v5.0.0-7.2.1 |

**Problem:** Es gibt keinen expliziten Logout-Endpoint! 

**Konsequenz:** 
- ❌ Token bleibt gültig bis Ablauf (1 Stunde)
- ❌ Kein Blacklist-Mechanismus
- ❌ Hochrisiko bei Token-Diebstahl

---

#### TC-V7-003: JWT Secret Strength
**Risiko:** 🔴 KRITISCH

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Überprüfe JWT Secret Stärke |
| **Methode** | Code-Review, application.properties |
| **Erwartet** | Secret ist >= 32 Zeichen, cryptographisch stark |
| **ASVS Requirement** | v5.0.0-7.3.2 |

**application.properties:**
```properties
jwt.secret=${JWT_SECRET}
```

**Problem:** Secret wird aus Umgebungsvariable gelesen - muss geprüft werden!

---

### **5. V8 - AUTHORIZATION & ACCESS CONTROL** 🔴 KRITISCH

**OWASP Top 10 2025 Mapping:** A01 - Broken Access Control

#### TC-V8-001: Broken Access Control - Andere User-Profile ändern
**Risiko:** 🔴 KRITISCH (IDOR - Insecure Direct Object Reference)

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Normaler User versucht, Admin-Profil zu ändern |
| **Vorbedingung** | Login als User (role=2), Token vorhanden |
| **Input** | `PUT /user/1` (Admin-ID) mit neuen Daten |
| **Erwartet** | 403 Forbidden - User kann nur sein eigenes Profil ändern |
| **Methode** | Postman |
| **ASVS Requirement** | v5.0.0-8.1.4 |

**Test-Durchführung:**

1. **Login als normaler User (ID=2):**
```bash
curl -X PUT http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username": "normaluser", "password": "password123"}'
```
Ergebnis: `{ "token": "eyJhbGc..." }` → Token speichern

2. **Versuche Admin-Profil (ID=1) zu ändern:**
```bash
curl -X PUT http://localhost:8080/user/1 \
  -H "Authorization: Bearer <TOKEN_NORMAL_USER>" \
  -H "Content-Type: application/json" \
  -d '{"username": "hacked", "email": "hacker@evil.com"}'
```

**Code-Analyse (UserController.java, Zeile 124-135):**
```java
int role = claims.get("role", Integer.class);
int userId = claims.get("userId", Integer.class);

if (!(role == 2 && userId == user.getId_user()) && role != 3) {
  return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
}
```

**Problem:** ⚠️ Logik-Error! Bedingung ist verwirrend:
- `role == 2` (normaler User) UND `userId == user.getId_user()` (sein eigen Profil) → OK
- `role == 3` (Admin) → OK
- Aber: Wenn User ID in der URL nicht der eigenen ID entspricht, sollte es blocked sein

**Erwartetes Resultat:** ✅ Sollte funktionieren (wird überprüft)

---

#### TC-V8-002: Role-based Access Control - GET /all mit normalem User
**Risiko:** 🔴 KRITISCH

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Normaler User versucht, alle User zu listern |
| **Vorbedingung** | Login als normaler User (role=2) |
| **Input** | `GET /user/all` mit User-Token |
| **Erwartet** | 403 Forbidden - nur Admins (role=3) dürfen das |
| **Methode** | Postman |
| **ASVS Requirement** | v5.0.0-8.2.1 |

**Test-Durchführung:**
```bash
curl -X GET http://localhost:8080/user/all \
  -H "Authorization: Bearer <TOKEN_NORMAL_USER>"
```

**Code-Analyse (UserController.java, Zeile 56-61):**
```java
int role = claims.get("role", Integer.class);
if (role != 2 && role != 3) { // user oder admin
  return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
}
```

**Problem:** ⚠️ LOGIK-ERROR!
- `role != 2 && role != 3` means: wenn Rolle NICHT User UND NICHT Admin → block
- Das bedeutet: User (role=2) UND Admin (role=3) sind ERLAUBT!
- Sollte sein: `role != 3` (nur Admin)

**Erwartetes Resultat:** ❌ VULNERABILITY - normaler User kann alle User sehen (BROKEN ACCESS CONTROL)

---

#### TC-V8-003: Privilege Escalation via Role-Manipulation
**Risiko:** 🔴 KRITISCH

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | User versucht, sein Rollenfeld im Update selbst zu ändern |
| **Input** | `PUT /user/2` mit `"role": 3` (Admin machen) |
| **Erwartet** | Role kann nicht geändert werden (ignoriert oder 400 error) |
| **Methode** | Postman |
| **ASVS Requirement** | v5.0.0-8.3.4 |

**Test-Durchführung:**
```bash
curl -X PUT http://localhost:8080/user/2 \
  -H "Authorization: Bearer <TOKEN_USER_ID_2>" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "normaluser",
    "email": "normal@test.com",
    "password": null,
    "role": 3
  }'
```

**Code-Analyse (UserDao.java):**
```java
public int update(int ID, User user) {
  // ... das update ignoriert das role-Feld!
  // Es updatet nur: username, email, password
}
```

**Erwartetes Resultat:** ✅ Role wird ignoriert (safe) - aber sollte explizit dokumentiert sein

---

### **6. V9 - SELF-CONTAINED TOKENS (JWT)** 🔴 KRITISCH

**OWASP Top 10 2025 Mapping:** A07 - Authentication Failures

#### TC-V9-001: JWT Algorithm Confusion
**Risiko:** 🔴 KRITISCH

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Versuche, JWT mit 'none' Algorithm zu dekodieren |
| **Input** | JWT Token mit `"alg": "none"` |
| **Erwartet** | Token wird REJECTED (nur HS256 erlaubt) |
| **Methode** | JWT.io, Postman |
| **ASVS Requirement** | v5.0.0-9.2.3 |

**Generiere manipulierten Token:**
```json
Header: {"alg": "none", "typ": "JWT"}
Payload: {"userId": 1, "username": "admin", "role": 3}
Signature: (leer)
```

**Code-Analyse (UserController.java):**
```java
Key secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
Jwts.parserBuilder()
  .setSigningKey(secretKey)
  .build()
  .parseClaimsJws(token); // ✅ verwendet JJWT 0.11.5 - sollte sicher sein
```

**Erwartetes Resultat:** ✅ Sollte gesch wird sein (JJWT blockiert 'none' standard)

---

#### TC-V9-002: JWT Secret Brute Force
**Risiko:** 🔴 KRITISCH (falls schwaches Secret)

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Versuche, JWT Secret zu knacken (Token neu signieren) |
| **Methode** | jwt-cracker Tool |
| **Erwartet** | Secret ist stark genug, bruteforce unmöglich (>128 bits) |
| **ASVS Requirement** | v5.0.0-9.3.1 |

**Problem:** Dies kann nur überprüft werden, wenn JWT_SECRET Environment-Variable gesetzt wird

---

#### TC-V9-003: JWT Claims Tampering
**Risiko:** 🔴 KRITISCH

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Ändere JWT Payload und versuche, Token zu verwenden |
| **Input** | Ändere `"role": 3` zu `"role": 99` |
| **Erwartet** | Token wird REJECTED (Signature ungültig) |
| **Methode** | jwt.io, Postman |
| **ASVS Requirement** | v5.0.0-9.1.1 |

**Test-Durchführung:**
1. Gültigen Token bekommen (Login)
2. Auf jwt.io dekodieren
3. Im Payload `"role"` ändern
4. Token kopieren (invalidiert Signature)
5. In Request nutzen: `GET /user/all` mit gefälschtem Token

**Erwartetes Resultat:** ✅ JwtException wegen ungültiger Signature

---

### **7. V11 - CRYPTOGRAPHY** 🔴 KRITISCH

#### TC-V11-001: Passwort-Hashing Algorithmus
**Risiko:** 🔴 KRITISCH

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Überprüfe, ob BCrypt verwendet wird |
| **Methode** | Code-Review |
| **Erwartet** | Passwörter werden mit BCrypt (min. cost=10) gehashed |
| **ASVS Requirement** | v5.0.0-11.3.1 |

**Code-Analyse (PasswordService.java):**
```java
private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

public String hashPassword(String plainPassword) {
  return passwordEncoder.encode(plainPassword); // ✅ BCrypt mit default cost=10
}
```

**Erwartetes Resultat:** ✅ BCrypt wird verwendet (SICHER)

---

#### TC-V11-002: Plaintext Passwords in Datenbank
**Risiko:** 🔴 KRITISCH

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Hole User-Daten direkt aus Datenbank |
| **Methode** | MySQL Client, Code-Review |
| **Erwartet** | Alle Passwörter in DB sind gehashed, NICHT plaintext |
| **ASVS Requirement** | v5.0.0-11.1.1 |

**Database-Check:**
```sql
SELECT username, password FROM user LIMIT 5;
```

**Erwartetes Resultat:**
```
username  | password
----------|-------
admin     | $2a$10$YIjlrHzf... (BCrypt Hash) ✅
testuser  | $2a$10$x9Kl2pQ... (BCrypt Hash) ✅
```

---

### **8. V13 - CONFIGURATION & SECRETS** 🟡 WICHTIG

#### TC-V13-001: Hardcodierte Secrets in Code
**Risiko:** 🔴 KRITISCH

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Überprüfe, ob Secrets in Code hardcodiert sind |
| **Methode** | Code-Suche nach "password", "secret", "api_key" |
| **Erwartet** | Alle Secrets kommen aus Umgebungsvariablen |
| **ASVS Requirement** | v5.0.0-13.1.2 |

**application.properties:**
```properties
spring.datasource.username=linus          // ⚠️ HARDCODIERT!
spring.datasource.password=Maria          // ⚠️ HARDCODIERT!
jwt.secret=${JWT_SECRET}                  // ✅ Aus Umgebung
```

**Erwartetes Resultat:** ❌ VULNERABILITY - DB-Credentials sind hardcodiert!

---

#### TC-V13-002: Debug-Mode in Production
**Risiko:** 🟡 MITTEL

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Überprüfe, ob Debug-Mode aktiv ist |
| **Methode** | Request mit Debug-Header |
| **Erwartet** | Debug-Modus ist deaktiviert |
| **ASVS Requirement** | v5.0.0-13.1.1 |

---

### **9. V14 - DATA PROTECTION** 🟡 WICHTIG

#### TC-V14-001: Passwörter in API Response
**Risiko:** 🔴 KRITISCH (bereits erwähnt in V6)

#### TC-V14-002: Sensitive Data Caching
**Risiko:** 🟡 MITTEL

| Kriterium | Wert |
|-----------|------|
| **Test-Beschreibung** | Überprüfe HTTP Cache-Header |
| **Methode** | Browser DevTools, Response-Headers |
| **Erwartet** | `Cache-Control: no-store, no-cache, private` für sensitive Endpoints |
| **ASVS Requirement** | v5.0.0-14.2.1 |

**Test-Durchführung:**
```bash
curl -I http://localhost:8080/user/all \
  -H "Authorization: Bearer <TOKEN>"
```

**Erwartetes Resultat:** Response-Header sollten enthalten:
```
Cache-Control: no-store, no-cache
Pragma: no-cache
```

---

## 📊 TESTABDECKUNGS-MATRIX

| ASVS Kapitel | Total Tests | Geplant | Durchgeführt | Status |
|--------------|-------------|---------|--------------|--------|
| V1 (Injection) | 5 | 3 | 0 | ⏳ |
| V2 (Input) | 4 | 3 | 0 | ⏳ |
| V6 (Auth) | 5 | 3 | 0 | ⏳ |
| V7 (Session) | 4 | 3 | 0 | ⏳ |
| V8 (Access) | 4 | 3 | 0 | ⏳ |
| V9 (JWT) | 3 | 3 | 0 | ⏳ |
| V11 (Crypto) | 2 | 2 | 0 | ⏳ |
| V13 (Config) | 2 | 2 | 0 | ⏳ |
| V14 (Data) | 2 | 2 | 0 | ⏳ |
| **GESAMT** | **31** | **24** | **0** | **⏳** |

---

## 🎯 OWASP TOP 10 2025 MAPPING

| Top 10 Risiko | ASVS Kapitel | Test Cases | Status |
|---------------|--------------|-----------|--------|
| A01: Broken Access Control | V8 | 3 | ⏳ |
| A02: Security Misconfiguration | V13, V12 | 2 | ⏳ |
| A04: Cryptographic Failures | V11, V14 | 2 | ⏳ |
| A05: Injection | V1 | 3 | ⏳ |
| A06: Insecure Design | V2 | 3 | ⏳ |
| A07: Authentication Failures | V6, V7, V9 | 6 | ⏳ |

---

## 📈 RISIKOMATRIX (Pro Test)

```
Eintrittswahrscheinlichkeit
        ↑
        |  🟢  🟡  🔴  🔴
(häufig)|
        |  🟢  🟡  🔴  🔴
(wahr-  |
schein-)|  🟢  🟡  🟡  🔴
lich)   |
        |  🟢  🟢  🟡  🟡
        └─────────────────→ Schadenspotenzial
        unwes. ger. krit. katastr.
```

**Legende:**
- 🟢 = Akzeptabel (Low Risk)
- 🟡 = ALARP-Bereich (Monitoring nötig)
- 🔴 = Inakzeptabel (MUSS gefixt werden)

---

## ✅ NÄCHSTE SCHRITTE

1. **[Schritt 5]** Statische Code-Analyse durchführen (SAST)
2. **[Schritt 6]** Dynamische Tests ausführen (DAST mit Postman/ZAP)
3. **[Schritt 7]** Schwachstellen dokumentieren (mit Screenshots, cURLs)
4. **[Schritt 8]** Finalen Testbericht erstellen

---

**Dokument Version:** 1.0  
**Letzte Änderung:** 28.01.2026  
**Status:** In Vorbereitung für Test-Durchführung
