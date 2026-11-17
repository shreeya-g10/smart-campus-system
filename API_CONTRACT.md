# Smart Campus – API Contract

This file is the agreement between both team members. Code your Feign clients and DTOs
against **this file**, not against the other person's code. Any change here must be
agreed by both before it is pushed.

All external calls go through the gateway: `http://localhost:8080/...`

## Ownership

| Project | Port | Eureka name | Owner |
|---|---|---|---|
| `eureka-server` | 3000 | `EUREKA-SERVER` | Shreeya |
| `resident-service` | 8081 | `RESIDENT-SERVICE` | Shreeya |
| `security-alert-service` | 8083 | `SECURITY-ALERT-SERVICE` | Shreeya |
| `gateway-service` | 8080 | `GATEWAY-SERVICE` | Vaidehi |
| `visitor-pass-service` | 8082 | `VISITOR-PASS-SERVICE` | Vaidehi |

Rule: only edit folders you own. Pull before you start, push when a piece works.

## Conventions

- Base package: `com.smartcampus.<service>` (`resident`, `visitor`, `security`, `gateway`, `eureka`)
- Timestamps are `LocalDateTime`, **set server-side** (clients send `null` / omit them).
- Enum-like fields are plain `String`s with the exact values listed below.
- Not found → HTTP `404`. Validation failure → HTTP `400`.
- DTOs used by Feign clients are **copied** into the calling project (no shared module).

---

## 1. Resident Service (`RESIDENT-SERVICE`, 8081) – Shreeya

```java
Resident {
  Long    id;
  String  name;
  String  roomNumber;   // e.g. "A-101"
  String  phone;
  String  email;
  boolean active;
}
```

| Method | Path | Body | Returns |
|---|---|---|---|
| GET | `/residents` | – | `List<Resident>` |
| GET | `/residents/{id}` | – | `Resident` or 404 |
| GET | `/residents/room/{roomNumber}` | – | `Resident` or 404 |
| POST | `/residents` | `Resident` (no id) | created `Resident` |

**Seed data** (inserted on startup by a `CommandLineRunner`):

| id | name | roomNumber | active |
|---|---|---|---|
| 1 | Aarav Sharma | A-101 | true |
| 2 | Diya Patel | A-102 | true |
| 3 | Rohan Iyer | B-205 | **false** (used to demo a rejected pass) |

---

## 2. Visitor Pass Service (`VISITOR-PASS-SERVICE`, 8082) – Vaidehi

```java
VisitorPass {
  Long          id;
  String        visitorName;
  String        visitorPhone;
  String        purpose;
  Long          hostId;        // Resident.id
  String        status;        // APPROVED | REJECTED | CHECKED_IN | CHECKED_OUT
  LocalDateTime createdAt;
  LocalDateTime checkInTime;
  LocalDateTime checkOutTime;
}
```

| Method | Path | Body | Returns |
|---|---|---|---|
| POST | `/visitors/passes` | `{visitorName, visitorPhone, purpose, hostId}` | `VisitorPass` (APPROVED or REJECTED) |
| GET | `/visitors/passes` | – | `List<VisitorPass>` |
| GET | `/visitors/passes/{id}` | – | `VisitorPass` or 404 |
| PUT | `/visitors/passes/{id}/checkin` | – | `VisitorPass` (CHECKED_IN) |
| PUT | `/visitors/passes/{id}/checkout` | – | `VisitorPass` (CHECKED_OUT) |
| GET | `/visitors/passes/active/host/{hostId}` | – | `List<VisitorPass>` with status APPROVED or CHECKED_IN |

**Rules**
- Create: call `GET /residents/{hostId}`.
  - Host exists **and** `active == true` → save `APPROVED`, then `POST /security/logs` with `eventType = PASS_CREATED`.
  - 404 or inactive → save `REJECTED`, then `POST /security/alerts` with `alertType = UNAUTHORIZED_HOST_ATTEMPT`.
- Check-in: only allowed from `APPROVED` (else 400). Sets `checkInTime`, logs `CHECK_IN`.
- Check-out: only allowed from `CHECKED_IN` (else 400). Sets `checkOutTime`, logs `CHECK_OUT`.

---

## 3. Security Alert Service (`SECURITY-ALERT-SERVICE`, 8083) – Shreeya

```java
SecurityLog {
  Long          id;
  Long          passId;
  String        visitorName;
  Long          hostId;
  String        eventType;   // PASS_CREATED | CHECK_IN | CHECK_OUT
  String        message;
  LocalDateTime timestamp;
}

SecurityAlert {
  Long          id;
  String        alertType;   // UNAUTHORIZED_HOST_ATTEMPT
  String        message;
  String        severity;    // LOW | MEDIUM | HIGH
  LocalDateTime timestamp;
}

RoomAudit {
  Resident          resident;
  List<VisitorPass> activeVisitors;
}
```

| Method | Path | Body | Returns |
|---|---|---|---|
| POST | `/security/logs` | `SecurityLog` (no id / timestamp) | created `SecurityLog` |
| GET | `/security/logs` | – | `List<SecurityLog>` |
| POST | `/security/alerts` | `SecurityAlert` (no id / timestamp) | created `SecurityAlert` |
| GET | `/security/alerts` | – | `List<SecurityAlert>` |
| GET | `/security/audit/room/{roomNumber}` | – | `RoomAudit` or 404 |

**Audit rule:** call `GET /residents/room/{roomNumber}`, then
`GET /visitors/passes/active/host/{resident.id}`, and return both.

---

## Who calls whom (Feign)

```
visitor-pass-service  ──► RESIDENT-SERVICE        GET  /residents/{id}
                      ──► SECURITY-ALERT-SERVICE  POST /security/logs, POST /security/alerts
security-alert-service ─► RESIDENT-SERVICE        GET  /residents/room/{roomNumber}
                      ──► VISITOR-PASS-SERVICE    GET  /visitors/passes/active/host/{hostId}
```
