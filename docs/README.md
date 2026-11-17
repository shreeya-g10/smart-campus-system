# Lab record – screenshots

Save screenshots in this folder with the exact file names below, so the links work on GitHub.
Take them after integration (all 5 services running, requests sent through `localhost:8080`).

| # | File name | What to capture | Shows |
|---|---|---|---|
| 1 | `01-eureka-dashboard.png` | <http://localhost:3000> with all 4 services **UP** | Service discovery |
| 2 | `02-residents-via-gateway.png` | `GET localhost:8080/residents` in Postman | Gateway routing + seed data |
| 3 | `03-pass-approved.png` | `POST /visitors/passes` with `hostId: 1` → `APPROVED` | Host validation (Feign → resident) |
| 4 | `04-pass-rejected.png` | `POST /visitors/passes` with `hostId: 3` → `REJECTED` | Inactive host blocked |
| 5 | `05-security-alerts.png` | `GET /security/alerts` showing `UNAUTHORIZED_HOST_ATTEMPT` | Feign → security alert |
| 6 | `06-checkin.png` | `PUT /visitors/passes/1/checkin` → `CHECKED_IN` | Gate check-in |
| 7 | `07-room-audit.png` | `GET /security/audit/room/A-101` with the active visitor | Security → resident + visitor |
| 8 | `08-checkout.png` | `PUT /visitors/passes/1/checkout` → `CHECKED_OUT` | Gate check-out |
| 9 | `09-security-logs.png` | `GET /security/logs` showing `PASS_CREATED`, `CHECK_IN`, `CHECK_OUT` | Full audit trail |
| 10 | `10-h2-residentdb.png` | H2 console `localhost:8081/h2-console`, `SELECT * FROM RESIDENTS` | Separate DB per service |
| 11 | `11-h2-visitordb.png` | H2 console `localhost:8082/h2-console`, passes table | Separate DB per service |
| 12 | `12-h2-securitydb.png` | H2 console `localhost:8083/h2-console`, `SELECT * FROM SECURITY_LOGS` | Separate DB per service |
| 13 | `13-openshift-topology.png` | OpenShift **Topology** view with 5 deployments | Cloud deployment |
| 14 | `14-openshift-eureka.png` | Eureka dashboard via its OpenShift route | Discovery in the cloud |
| 15 | `15-openshift-gateway.png` | Postman call to the gateway's public route URL | Public API in the cloud |

## Gallery

<!-- Remove the comment markers once the images exist. -->
<!--
![Eureka dashboard](01-eureka-dashboard.png)
![Residents via gateway](02-residents-via-gateway.png)
![Pass approved](03-pass-approved.png)
![Pass rejected](04-pass-rejected.png)
![Security alerts](05-security-alerts.png)
![Check-in](06-checkin.png)
![Room audit](07-room-audit.png)
![Check-out](08-checkout.png)
![Security logs](09-security-logs.png)
-->
