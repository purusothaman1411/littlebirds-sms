# Little Birds School Management System: Target Architecture

Status: living document (all 19 phases complete; see section 17 for the module screens).

## 1. Repository layout

```
littlebirds-sms/
├── legacy-console/   Original Core Java console app (reference; kept until web app reaches parity)
├── backend/          Spring Boot 3.x REST API (Java 17+, Maven)
├── frontend/         React + React Router (Vite), plain JavaScript
└── docs/             Architecture, DB design, API notes
```

`legacy-console/data/` is git-ignored: it holds plaintext passwords and personal details.

## 2. Layers and request flow

```
React (pages -> services/api.js)
   | HTTPS, JSON, Authorization: Bearer <JWT>
REST  /api/**
   |
Controller   HTTP only: parse, @Valid, call service, return DTO + status
   |
Service      ALL business rules; @Transactional; authorization of subject-staff scope
   |
Repository   Spring Data JPA interfaces only
   |
MySQL
```

Rules:
- Controllers never touch repositories; entities never leave the service layer (DTOs only).
- Each business rule exists once (grade bands, pass mark 35, curriculum lookup, subject-staff scope).
- Backend is the final authority on authorization. React only hides UI.

## 3. Backend packages (`com.littlebirds.sms`)

| Package | Responsibility |
|---|---|
| `controller` | REST endpoints |
| `service` | business logic |
| `repository` | Spring Data JPA |
| `entity` | JPA entities and enums (`Role`, `Gender`, `AttendanceStatus`) |
| `dto` | request/response objects, `ApiError` |
| `exception` | the 5 legacy exceptions + `GlobalExceptionHandler` |
| `security` | `SecurityConfig`, JWT filter/service, `PermissionMatrix` |
| `config` | CORS (dev), first-run seeder |

## 4. Business rules carried over from the console app

**Curriculum** (stored as data in `curriculum`, not hardcoded)

| Level / group | Subjects |
|---|---|
| Std 1-10, no group | Tamil, English, Mathematics, Science, Social Science |
| 11-12 Bio-Maths | Tamil, English, Physics, Chemistry, Mathematics, Biology |
| 11-12 Computer Science | Tamil, English, Physics, Chemistry, Mathematics, Computer Science |
| 11-12 Commerce | Tamil, English, Accountancy, Commerce, Economics, Computer Applications |
| 11-12 Humanities | Tamil, English, History, Economics, Political Science |

**Results**: percentage = total / (subjects x 100) over ALL curriculum subjects; grade A+ >=90, A >=80, B >=70, C >=60, D >=50, else F; PASS only if every subject >= 35. A student with missing subjects is reported as INCOMPLETE (fixes the legacy partial-marks bug).

**Student**: ID is a String, letter first then letters/digits/`_`/`-`, max 20; standard 1-12; group required for 11-12 and must be empty for 1-10; age is computed from DOB.

**Teacher**: ID is a String; contact is exactly 10 digits; valid email; required fields non-empty. No age field.

**Attendance**: one record per student per date (P/A); no future dates; percentage = present / recorded days.

**Deleting a student** cascades to marks and attendance.

## 5. Authorization (enforced in backend)

Fixed roles (enum, no roles table): HEADMASTER, SUBJECT_STAFF, WORKING_STAFF, MANAGEMENT_STAFF. Permissions are one matrix in `PermissionMatrix`, mapped to Spring Security authorities and enforced with `@PreAuthorize`.

| Authority | HEADMASTER | SUBJECT_STAFF | WORKING_STAFF | MANAGEMENT_STAFF |
|---|:-:|:-:|:-:|:-:|
| STUDENT_VIEW, TEACHER_VIEW, MARKS_VIEW, ATTENDANCE_VIEW, REPORT_CLASS_VIEW | ✓ | ✓ | ✓ | ✓ |
| STUDENT_ADD, STUDENT_UPDATE | ✓ | | ✓ | |
| STUDENT_DELETE | ✓ | | | |
| TEACHER_ADD / UPDATE / DELETE | ✓ | | | |
| MARKS_ADD, MARKS_UPDATE | ✓ | ✓ (own subjects, checked in service) | | |
| ATTENDANCE_MARK | ✓ | ✓ | ✓ | |
| REPORT_CARD_VIEW | ✓ | ✓ | | ✓ |
| STAFF_MANAGE (new) | ✓ | | | |

Subject-staff rule (unchanged from console): matched by subject name against the staff member's assigned subjects. `class_range` and `group_name` on staff are stored but not enforced yet.

## 6. Authentication

- `POST /api/auth/login` with username + password. Password verified with BCrypt.
- Returns a signed JWT (role and staff id as claims) plus the user profile. Stateless; no server session.
- Secret, expiry and the first Headmaster's credentials come from environment variables. Nothing is hardcoded.
- First run: if `staff_users` is empty, the seeder creates one HEADMASTER from `SMS_ADMIN_USERNAME` / `SMS_ADMIN_PASSWORD`.

## 7. REST API outline

All under `/api`. Errors use one shape.

```
POST   /auth/login            GET /auth/me
GET    /students?search=&standard=&page=&size=     GET/PUT/DELETE /students/{id}     POST /students
GET    /teachers?subject=&page=&size=              GET/PUT/DELETE /teachers/{id}     POST /teachers
GET    /staff   POST /staff   GET/PUT/DELETE /staff/{id}     PUT /staff/{id}/password
GET    /subjects?standard=&group=                  (curriculum lookup)
GET    /students/{id}/marks       PUT /students/{id}/marks     (upsert per subject)
GET    /marks/highest?subject=    GET /marks/passed    GET /marks/above?percent=50    GET /marks/ranking
POST   /attendance                (class + date + list of P/A)   GET /attendance?studentId=|date=|standard=
GET    /reports/report-card/{studentId}     GET /reports/class-results?standard=
```

Error body:
```json
{ "status": 409, "message": "Student ID already exists", "timestamp": "2026-09-30T10:00:00Z", "errors": { } }
```
`errors` is only filled for validation failures (field -> message). No stack traces.

Exception mapping: `*NotFoundException` -> 404, `*AlreadyExistsException` -> 409, `InvalidMarkException` and validation failures -> 400, bad credentials -> 401, missing authority -> 403.

## 8. Frontend structure

```
frontend/src/
  services/   api.js (single fetch wrapper: base URL, token, error parsing) + one file per module
  context/    AuthContext (user, role, login, logout)
  routes/     AppRoutes, ProtectedRoute (role-aware)
  hooks/      usePermissions (mirrors the PermissionMatrix, UI only)
  components/ Layout, Topbar, TopNav, DataTable, ConfirmDialog, FormField, ...
  pages/      Login, Dashboard, Students, Teachers, Staff, Marks, Attendance, Reports, Profile, Unauthorized
```

Dev: Vite proxies `/api` to the backend, so there are no CORS issues in development.

## 9. Configuration

Backend `application.properties` reads from environment: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `SMS_ADMIN_USERNAME`, `SMS_ADMIN_PASSWORD`. A `backend/.env.example` documents them; real `.env` files are git-ignored.

## 10. Phase gates ("runnable after each stage")

| Phase | Runnable check |
|---|---|
| 3 | `schema.sql` runs cleanly on an empty MySQL database |
| 4-6 | app boots and connects; service tests pass |
| 7-8 | endpoints callable with curl; validation errors match the shape above |
| 9 | each role gets 200/403 exactly as the matrix says |
| 10+ | `npm run dev` shows the app against the live API |

## 11. Database (Phase 3)

Files: `backend/db/schema.sql` (tables, keys, constraints) and `backend/db/seed.sql` (subjects + curriculum only).

| Table | Key | Notes |
|---|---|---|
| `subjects` | `id`, unique `name` | 15 subjects |
| `curriculum` | unique (`level`, `group_name`, `subject_id`) | `group_name` = `'NONE'` for std 1-10 |
| `staff_users` | `staff_id`, unique `username` | BCrypt hash; role checked |
| `staff_subjects` | (`staff_id`, `subject_id`) | many-to-many, cascades on staff delete |
| `students` | `student_id` | group NULL for 1-10, required for 11-12 (CHECK); age not stored |
| `teachers` | `teacher_id` | no age column |
| `teacher_subjects` | (`teacher_id`, `subject_name`) | free text, as in the console app |
| `marks` | unique (`student_id`, `subject_id`) | 0-100 CHECK; cascades on student delete |
| `attendance` | unique (`student_id`, `attendance_date`) | P/A CHECK; cascades on student delete |

Notes:
- Roles, gender and status are `VARCHAR` + `CHECK`, not MySQL `ENUM`, so JPA `@Enumerated(STRING)` validates cleanly.
- Contact numbers keep the console rule of 10 digits starting 6-9.
- Requires MySQL 8.0.16+ (CHECK constraints are enforced from that version).
- Hibernate will run with `ddl-auto=validate`: the SQL files are the source of truth for the schema.

## 12. Implemented endpoints (Phase 7)

Attendance lookups were split into two clearer endpoints than the outline in section 7.

| Area | Endpoints |
|---|---|
| Students | `GET /api/students?name=&standard=&page=&size=`, `GET/PUT/DELETE /api/students/{id}`, `POST /api/students` |
| Teachers | `GET /api/teachers?subject=&page=&size=`, `GET /api/teachers/more-than-two-subjects`, `GET/PUT/DELETE /api/teachers/{id}`, `POST /api/teachers` |
| Staff | `GET /api/staff`, `GET/PUT/DELETE /api/staff/{id}`, `POST /api/staff`, `PUT /api/staff/{id}/password` |
| Lookups | `GET /api/groups`, `GET /api/subjects?standard=&group=` |
| Marks | `GET/PUT /api/students/{id}/marks`, `GET /api/marks/highest?subject=`, `/passed`, `/above?percent=50`, `/ranking` |
| Attendance | `POST /api/attendance`, `GET /api/attendance?date=&standard=`, `GET /api/students/{id}/attendance` |
| Reports | `GET /api/reports/report-card/{studentId}`, `GET /api/reports/class-results?standard=` |

Dates are ISO (`yyyy-MM-dd`). The acting staff member comes from the login token (Phase 9); the old temporary `X-Staff-Id` header no longer exists.

## 13. Validation and errors (Phase 8)

Format rules live on the request DTOs (Bean Validation), taken from the console app's `InputHelper`; business rules stay in the services. Create endpoints use the `OnCreate` group (so the ID in the body is required on create and ignored on update).

| Status | When | Body |
|---|---|---|
| 400 | Validation failed | `{"status":400,"message":"Validation failed","timestamp":"...","errors":{"contactNumber":"Contact number must be exactly 10 digits and start with 6-9"}}` |
| 400 | Broken business rule, invalid mark, malformed JSON, missing/invalid parameter | `{"status":400,"message":"...","timestamp":"..."}` |
| 403 | Role or subject not allowed | same shape |
| 404 | Student, teacher, staff or marks not found; unknown URL | same shape |
| 405 / 415 | Wrong method / content type | same shape |
| 409 | ID or username already exists; database conflict | same shape |
| 500 | Anything unexpected (details only in the server log) | generic message, no stack trace |

Deliberately not done with annotations: "date of birth / attendance date not in the future" is checked in the service using the school's time zone (Asia/Kolkata). `@PastOrPresent` would use the server's zone and reject valid dates for the first 5.5 hours of each Indian day on a UTC server.

## 14. Authentication and authorization (Phase 9)

**Login**: `POST /api/auth/login` `{username, password}` returns `{token, tokenType:"Bearer", expiresAt, user:{staffId,name,username,role,permissions}}`. `GET /api/auth/me` returns the current user (used by React to restore a session). Every other endpoint except `GET /api/health` needs `Authorization: Bearer <token>`.

**Token**: HS256 JWT signed with `JWT_SECRET` (>= 32 chars, startup fails otherwise), subject = staff ID, expiry `JWT_EXPIRY_MINUTES` (default 480). Built with Spring's own oauth2-resource-server support, no extra library.

**Checked on every request**: signature and expiry, then the account is loaded from the database. A deleted account gets 401 at once; a changed role applies at once. Authorities come from `PermissionMatrix`, never from the token.

**Enforcement**: `@PreAuthorize("hasAuthority('...')")` on each controller method (`StaffController`: class level `STAFF_MANAGE`). Subject-staff "own subjects only" stays in `MarksService`.

| Status | Meaning |
|---|---|
| 401 | missing, invalid or expired token; wrong username/password (one message for both) |
| 403 | valid login, role lacks the permission |

**First run**: if `staff_users` is empty, `AdminSeeder` creates one HEADMASTER from `SMS_ADMIN_USERNAME` / `SMS_ADMIN_PASSWORD`. Passwords are BCrypt-hashed; none are in code or SQL.

**Logout** is client-side (React deletes the token). **Not done yet**: login rate limiting / lockout; token revocation before expiry.

## 15. Frontend shell (Phase 10)

Vite + React 18 + React Router 6, plain JavaScript, no UI library (one `index.css`). Dev server on 5173; Vite proxies `/api` to `http://localhost:8080` (`VITE_API_PROXY` to change).

- `services/api.js`: the only `fetch` call. Adds `Authorization: Bearer`, turns error bodies into `ApiError(status, message, errors)`, and calls a registered handler on any 401 from a protected call.
- Token lives in `sessionStorage` (cleared when the tab closes) plus memory. After a refresh, `AuthContext` calls `GET /api/auth/me` to confirm it is still valid.
- `usePermissions` reads the permission list returned by the server at login, so the permission matrix exists only in the backend. It only hides UI; the backend still enforces.
- `ProtectedRoute` takes `permission` or `anyOf`: not logged in goes to `/login` (and back after login); logged in without permission goes to `/unauthorized`.
- Top menu items come from `components/navItems.js` and are filtered by permission.
- Module pages were placeholders until Phases 12-17; all are now built (section 17).

## 16. Login and dashboard (Phase 11)

- Login: client-side checks (both fields required), show/hide password, backend message shown on failure (the same text for wrong username and wrong password), password field cleared after a failure, returns to the page originally requested.
- Dashboard: total cards for Students, Teachers (and Staff accounts for the Headmaster) read from the paged list endpoints with `size=1` (`totalElements`) and `GET /api/staff`. Only totals the role may see are requested; each is independent, so one failure shows "–" and the rest still load. Quick links come from `navItems.js` filtered by permission.

## 17. Module screens (Phases 12-17)

Each module has a service file in `frontend/src/services/` and a page in `frontend/src/pages/`. Every route is wrapped in `ProtectedRoute` with the permission it needs; buttons inside a page use `usePermissions()` and only hide what the backend would refuse anyway.

| Route | Page | Permission | Notes |
|---|---|---|---|
| `/students` | Students | STUDENT_VIEW | search by name, filter by standard, paged; add/edit/delete by permission; group shown only for standards 11-12 |
| `/teachers` | Teachers | TEACHER_VIEW | filter by subject (exact name), "more than 2 subjects" list; subjects typed as comma-separated text |
| `/staff` | Staff | STAFF_MANAGE | accounts, reset password; subjects chosen from the curriculum list; cannot delete own account |
| `/marks` | Marks | MARKS_VIEW | per-student marks (only changed subjects are sent), ranking, passed, above %, highest per subject |
| `/attendance` | Attendance | ATTENDANCE_VIEW | mark class (ATTENDANCE_MARK), by date, by student |
| `/reports` | Reports | REPORT_CARD_VIEW or REPORT_CLASS_VIEW | printable report card, class results with rank and class average |

Subjects for dropdowns come from `/api/subjects` (it needs a standard, so `subjectService.js` asks for standard 1 and for standard 11 in each group and merges the answers).

## 18. Known limits

- The frontend has not been covered by automated tests; the backend has unit tests for validation, results and the permission table.
- The login profile does not include a staff member's assigned subjects, so Subject Staff see mark boxes for all subjects of a student; the backend rejects subjects they are not assigned to.
- Staff `classRange` and `group` are stored but not enforced.
- Teachers have no link to classes or to the subjects table (free-text subjects, as in the console app).
- A mark cannot be removed once entered, only changed.
