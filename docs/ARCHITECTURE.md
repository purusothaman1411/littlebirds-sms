# Little Birds School Management System: Target Architecture

Status: Phase 2 (design). No implementation code yet.

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
  components/ Layout, Sidebar, Topbar, DataTable, ConfirmDialog, FormField, ...
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
