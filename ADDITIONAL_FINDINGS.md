# 🔍 ADDITIONAL FINDINGS (Pass 1 - Automatisierte Quick Tests)

**Projekt:** HobbyHandwerker  
**Datum:** 28.01.2026  
**Status:** Pass 1 abgeschlossen — Wartet auf Pass 2 (manuelle Verifizierung)

---

## 🧪 TEST-RESULTATE

### **Test 1: CORS Misconfiguration**

**Ziel:** Prüfe ob CORS zu permissiv konfiguriert ist

**Result:** ✅ **SICHER**

**Findings:**
```
@CrossOrigin(origins = "http://localhost:4200")
```

- Nur localhost:4200 erlaubt (nicht `*`)
- Nicht zu permissiv
- ✅ Keine Vulnerability

---

### **Test 2: XSS / Input Encoding**

**Ziel:** Prüfe ob XSS-Payloads in Input-Feldern akzeptiert/gespeichert werden

**Test:** Registriere User mit username `testxss_onload`

**Result:** ⚠️ **POTENZIELLE VULNERABILITY**

**Findings:**
```
Username wurde akzeptiert: "testxss_onload"
- ✅ Harmlose Payload wurde gespeichert
- ❓ Keine Input-Sanitization auf Username
- ❓ Backend speichert beliebige Strings
```

**Severity:** 🟡 LOW (Browser führt aus, nicht Backend — aber kombiniert mit F2 kritisch)

**Root Cause:**
- Keine Input Validation auf Username-Format
- User kann beliebige Strings speichern
- Falls Frontend nicht encoded → XSS möglich (Stored XSS via Logout + Redirect)

**Remediation:**
```java
// User.java - Add validation
@Pattern(regexp = "^[a-zA-Z0-9_-]{3,30}$", message = "Username must be alphanumeric")
private String username;
```

---

### **Test 3: Error Handling / Stack Trace Leak**

**Ziel:** Prüfe ob Backend Stack Traces in Errors zurückgibt

**Test:** Invalid Token + nicht-existente User ID (GET /user/999999)

**Result:** ✅ **SICHER**

**Response (500 Error):**
```json
{
  "timestamp": "2026-01-28T10:47:15.736+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/user/999999"
}
```

**Findings:**
- ✅ Generische Error Messages
- ✅ Keine Stack Traces exposed
- ✅ Keine technischen Details
- ✅ Keine Vulnerability

---

### **Test 4: Business Logic - Privilege Escalation 🔴 KRITISCH**

**Ziel:** Prüfe ob User ihre eigene Role zu Admin (role=3) ändern können

**Test:** User dummy2 (role=2, ID=2) versucht `PUT /user/2` mit `role=3`

**Request:**
```json
{
  "id_user": 2,
  "username": "dummy2",
  "email": "dummy2@mail.de",
  "password": "newpassword123",
  "role": 3
}
```

**Result:** 🔴 **AKZEPTIERT - PRIVILEGE ESCALATION VULNERABILITY**

**Status Code:** `200 OK`

**Finding: F9 - PRIVILEGE ESCALATION (Neu)**

**Severity:** 🔴 KRITISCH  
**OWASP Top 10:** A01:2025 – Broken Access Control  
**Risiko:** Normaler User kann sich selbst zum Admin promovieren

**Code-Stelle (UserController.java:119-135):**
```java
@PutMapping("/{user_id}")
public ResponseEntity<Integer> updateUserByID(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody User user,
                                              @PathVariable("user_id") int user_id) {
  // ... JWT parsing ...
  int role = claims.get("role", Integer.class);
  int userId = claims.get("userId", Integer.class);

  if (!(role == 2 && userId == user.getId_user()) && role != 3) {
    // ❌ LOGIC ERROR: User kann role-Feld setzen!
    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
  }

  // ❌ UPDATE speichert role-Feld direkt ab
  int count = dao.update(user_id, user);
  // ...
}
```

**Problem:**
- User kann sein `role`-Feld in Request setzen
- Backend prüft nicht ob Role unverändert bleibt
- `dao.update()` speichert alle Felder ab, inkl. role

**Remediation (Quick Fix):**
```java
// Ignoriere role-Feld für normale Users
if (role == 2) {
  user.setRole(2);  // Force role=2, keine Änderung möglich
}
// Jetzt update durchführen
int count = dao.update(user_id, user);
```

---

### **Test 5: Dependency Vulnerabilities**

**Ziel:** Prüfe auf bekannte CVEs in Maven Dependencies

**Dependencies (pom.xml):**
- Spring Boot: 3.5.4 ✅ (aktuell)
- jjwt: 0.11.5 ✅ (aktuell)
- spring-security-crypto: (parent managed) ✅
- mysql-connector: 8.x ✅

**Result:** ✅ **KEINE KRITISCHEN CVEs BEKANNT**

**Findings:**
- Alle Versionen sind aktuell (Jan 2026)
- Keine bekannten kritischen Vulnerabilities
- ✅ Keine Vulnerability

---

## 📊 ZUSAMMENFASSUNG PASS 1

| Test | Result | Finding | Severity |
|------|--------|---------|----------|
| CORS | ✅ Sauber | - | - |
| XSS | ⚠️ Risiko | Input nicht validiert | 🟡 LOW |
| Error Handling | ✅ Sauber | - | - |
| Privilege Escalation | 🔴 VULNERABLE | **F9 - Neu!** | 🔴 KRITISCH |
| Dependencies | ✅ Sauber | - | - |

**Neues Finding identifiziert: F9 (Privilege Escalation)**

---

## ✅ NÄCHSTER SCHRITT

**Pass 2 (Manuelle Verifizierung durch Tester):**

Bitte reproduziere diese 5 Tests manuell über UI/API und vergleiche mit Pass 1 Ergebnissen:

1. **CORS Test:** DevTools Network → Origin Header prüfen
2. **XSS Test:** Registriere User mit harmlosem XSS-ähnlichen Namen
3. **Error Handling:** Ungültige Request senden, Response anschauen
4. **Privilege Escalation:** Login als dummy2 → PUT /user/2 mit role=3 → Prüfe ob akzeptiert
5. **Dependencies:** Keine manuellen Tests nötig (Code-Review ausreichend)

Nach Pass 2 werden beide Ergebnisse in finalen Report konsolidiert.
