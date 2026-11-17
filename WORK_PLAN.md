# Smart Campus – Work Plan (Shreeya & Vaidehi)

Endpoints, DTO fields and seed data are defined in [API_CONTRACT.md](API_CONTRACT.md).
This file says **who builds what** and **how we avoid merge conflicts**.

---

## Merge-conflict rules (follow every time)

1. **Strict folder ownership**

   | Owner | May edit |
   |---|---|
   | **Shreeya** | `eureka-server/`, `resident-service/`, `security-alert-service/`, and root files (`pom.xml`, `.gitignore`, `API_CONTRACT.md`, `WORK_PLAN.md`, `README.md`, `docs/`) |
   | **Vaidehi** | `gateway-service/`, `visitor-pass-service/`, `postman/` |

2. **Never "quickly fix" the other person's code**, not even a typo. Message them instead.
3. **Root files are Shreeya's.** Vaidehi requests changes to the contract / `.gitignore`; both agree, then Shreeya edits.
4. **Pull before starting work (⌘T) and pull again before every push.**
5. **Commit only your own files.** In the commit window (⌘K) tick only files inside your folders. If a file you don't own appears, untick it and **Rollback** it.
6. **No project-wide Reformat Code / Optimize Imports.** Run them on your own folder only.
7. **Never commit `.idea/`, `target/`, `*.iml`.** `.gitignore` blocks them; run configurations stay local.
8. **DTOs are copied, not shared.** If you need another service's object, create your own DTO inside your service. No shared module.
9. **Only push code that compiles.** Run your service once before pushing.

### Branch workflow
- Each person works on their own branch (`shreeya-dev`, and Vaidehi's own branch). Never commit directly to `main`.
- When a step works: push your branch → open a **pull request into `main`** on GitHub → merge it.
- After the other person merges, bring their work into your branch: `git pull origin main` (while on your branch).
- Because each person only touches their own folders, PRs merge cleanly.

---

## Handoff points

| # | Pushed by | What | Unblocks |
|---|---|---|---|
| H1 | Shreeya | `resident-service` with seed data + `GET /residents/{id}` | Vaidehi's host validation |
| H2 | Shreeya | `POST /security/logs` and `POST /security/alerts` | Vaidehi's security notifications |
| H3 | Vaidehi | `GET /visitors/passes/active/host/{hostId}` | Shreeya's room audit |

Until a handoff arrives, wrap your Feign calls in `try/catch` so your service still works and can be tested alone. After the handoff, pull and test the real call.

---

## Shreeya

### S1. resident-service (8081) — package `com.smartcampus.resident`
1. `entity/Resident.java` — `@Entity`: `id` (`@Id @GeneratedValue`), `name`, `roomNumber` (`@Column(unique = true)`), `phone`, `email`, `active`.
2. `repository/ResidentRepository.java` — `extends JpaRepository<Resident, Long>` + `Optional<Resident> findByRoomNumber(String roomNumber)`.
3. `controller/ResidentController.java` — `@RestController @RequestMapping("/residents")`:
   - `GET /` → all
   - `GET /{id}` → 404 via `ResponseStatusException(HttpStatus.NOT_FOUND)` if missing
   - `GET /room/{roomNumber}` → 404 if missing
   - `POST /` → save
4. `config/DataSeeder.java` — `CommandLineRunner` inserting the 3 residents from the contract (A-101, A-102, B-205; B-205 `active=false`).
5. Test `localhost:8081/residents` and `localhost:8080/residents`; check H2 console `localhost:8081/h2-console` (JDBC URL `jdbc:h2:mem:residentdb`).
6. Commit + push → tell Vaidehi **H1 done**.

### S2. security-alert-service: logs & alerts (8083) — package `com.smartcampus.security`
1. `entity/SecurityLog.java`, `entity/SecurityAlert.java` — fields from the contract; `@PrePersist` sets `timestamp = LocalDateTime.now()`.
2. `SecurityLogRepository`, `SecurityAlertRepository`.
3. `controller/SecurityController.java` — `@RequestMapping("/security")`: `POST`/`GET` `/logs`, `POST`/`GET` `/alerts`.
4. Test with Postman (POST a log, GET the list).
5. Commit + push → tell Vaidehi **H2 done**.

### S3. security-alert-service: room audit (Feign)
1. `@EnableFeignClients` on `SecurityAlertServiceApplication`.
2. `dto/ResidentDto.java`, `dto/VisitorPassDto.java`, `dto/RoomAudit.java` (`resident`, `activeVisitors`).
3. `client/ResidentClient.java` — `@FeignClient(name = "RESIDENT-SERVICE")`, `GET /residents/room/{roomNumber}`.
4. `client/VisitorPassClient.java` — `@FeignClient(name = "VISITOR-PASS-SERVICE")`, `GET /visitors/passes/active/host/{hostId}`.
5. `controller/AuditController.java` — `GET /security/audit/room/{roomNumber}`:
   - `FeignException.NotFound` from resident → 404
   - visitor call fails (H3 not there yet) → return empty list
6. Push. After H3, pull and test `localhost:8080/security/audit/room/A-101`.

### S4. Documentation
1. `README.md` — overview, architecture diagram, run order (Eureka first), ports, test URLs.
2. `docs/` — screenshots (Eureka dashboard, H2 consoles, Postman results) for the lab record.

---

## Vaidehi

### V1. gateway-service (8080)
Routes are already configured.
1. Pull, run Eureka + gateway + one service, confirm routing through 8080.
2. Optional (good for demo): `filter/LoggingFilter.java` — a `GlobalFilter` logging method, path and response status of every request (proves all traffic goes through the gateway).
3. Commit + push.

### V2. visitor-pass-service: CRUD (8082) — package `com.smartcampus.visitor`
1. `entity/VisitorPass.java` — fields from the contract; `@PrePersist` sets `createdAt`.
2. `repository/VisitorPassRepository.java` — `List<VisitorPass> findByHostIdAndStatusIn(Long hostId, List<String> statuses)`.
3. `controller/VisitorPassController.java` — `@RequestMapping("/visitors/passes")`:
   - `GET /` → all
   - `GET /{id}` → 404 if missing
   - `GET /active/host/{hostId}` → status `APPROVED` or `CHECKED_IN`
4. Commit + push → tell Shreeya **H3 done**.

### V3. visitor-pass-service: pass logic + Feign
1. `@EnableFeignClients` on `VisitorPassServiceApplication`.
2. DTOs: `ResidentDto`, `SecurityLogRequest`, `SecurityAlertRequest`, `CreatePassRequest`.
3. `client/ResidentClient.java` — `@FeignClient(name = "RESIDENT-SERVICE")`, `GET /residents/{id}`.
4. `client/SecurityAlertClient.java` — `@FeignClient(name = "SECURITY-ALERT-SERVICE")`, `POST /security/logs`, `POST /security/alerts`.
5. `service/VisitorPassService.java`:
   - **create** — call `residentClient.getById(hostId)`
     - found and `active` → save `APPROVED` → log `PASS_CREATED`
     - `FeignException.NotFound` or inactive → save `REJECTED` → alert `UNAUTHORIZED_HOST_ATTEMPT`, severity `HIGH`
   - **checkin** — only from `APPROVED` (else 400) → set `checkInTime`, status `CHECKED_IN` → log `CHECK_IN`
   - **checkout** — only from `CHECKED_IN` (else 400) → set `checkOutTime`, status `CHECKED_OUT` → log `CHECK_OUT`
   - Wrap every security call in `try/catch` so a pass still saves if security-alert-service is down.
6. Controller: `POST /`, `PUT /{id}/checkin`, `PUT /{id}/checkout`.
7. After H1 + H2, pull and test through `localhost:8080`.
8. Commit + push.

### V4. Postman collection
`postman/smart-campus.postman_collection.json` (Postman → Export → Collection v2.1). All requests use `http://localhost:8080` and cover the integration scenarios below.

---

## Integration (both, on one laptop)

1. Both commit + push everything, then both pull.
2. Run order: **Eureka → resident → security → visitor → gateway**. Wait ~30 s before testing through the gateway.
3. Scenarios (all through `localhost:8080`):

| # | Request | Expected |
|---|---|---|
| 1 | `GET /residents` | 3 seeded students |
| 2 | `POST /visitors/passes` with `hostId=1` | `APPROVED`; `GET /security/logs` shows `PASS_CREATED` |
| 3 | `POST /visitors/passes` with `hostId=3` (inactive) | `REJECTED`; `GET /security/alerts` shows `UNAUTHORIZED_HOST_ATTEMPT` |
| 4 | `POST /visitors/passes` with `hostId=99` | `REJECTED` + alert |
| 5 | `PUT /visitors/passes/{id}/checkin` | `CHECKED_IN` + `CHECK_IN` log |
| 6 | `GET /security/audit/room/A-101` | resident 1 + the active visitor |
| 7 | `PUT /visitors/passes/{id}/checkout` | `CHECKED_OUT` + `CHECK_OUT` log |
| 8 | `GET /security/audit/room/A-101` again | empty `activeVisitors` |
| 9 | Check-in on a `REJECTED` pass | 400 |

4. A failing scenario is fixed by **the owner of that folder**, who pushes; the other person pulls.

---

## Phase 2: OpenShift

### Shreeya
1. Import `eureka-server` from Git — **Context Dir** `/eureka-server`, **Target port** `3000`, name exactly `eureka-server`. Deploy it first.
2. Import `resident-service` (port 8081) and `security-alert-service` (port 8083), each with env var `EUREKA_URI = http://eureka-server:3000/eureka/`.
3. No routes for those two. Create a **Route on `eureka-server`** for the demo dashboard.

### Vaidehi
1. Import `visitor-pass-service` (port 8082) and `gateway-service` (port 8080), each with the same `EUREKA_URI`.
2. Create a **Route on `gateway-service`** — the single public URL.
3. Duplicate the Postman environment with the route URL as base URL; re-run all 9 scenarios.

### Both
- Confirm the cloud Eureka dashboard shows 4 services UP.
- If using one sandbox account, add the other person under **Project → Access** (or share the login); otherwise one person deploys all 5 using the steps above.
