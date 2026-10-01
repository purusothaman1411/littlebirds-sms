# Little Birds School Management System

Web version of a Core Java console application: **Spring Boot** REST API + **React** UI + **MySQL**.

| Folder | What it is |
|---|---|
| `backend/` | Spring Boot 3 REST API (Java 17, Maven), SQL scripts in `backend/db/` |
| `frontend/` | React 18 + React Router, built with Vite |
| `legacy-console/` | The original console app, kept for reference only |
| `docs/ARCHITECTURE.md` | Design, business rules, API list |

## What it does

- **Students, Teachers**: list, search, add, edit, delete.
- **Staff accounts** (Headmaster only): create logins, set roles and subjects, reset passwords.
- **Marks**: enter and view marks per student, with total, percentage, grade and PASS/FAIL; ranking, passed students, students above a percentage, highest mark in a subject.
- **Attendance**: mark a whole class for a date, view by date or by student.
- **Reports**: printable report card, class results.

### Who can do what

| | Headmaster | Subject Staff | Working Staff | Management Staff |
|---|:-:|:-:|:-:|:-:|
| View students, teachers, marks, attendance, class results | yes | yes | yes | yes |
| Add / edit students | yes | | yes | |
| Delete students; add / edit / delete teachers | yes | | | |
| Enter / change marks | yes (any subject) | own subjects only | | |
| Mark attendance | yes | yes | yes | |
| Report cards | yes | yes | | yes |
| Staff accounts | yes | | | |

The backend checks every request. The UI only hides what a role cannot use.

## What you need

- Java 17 or newer, and Maven
- MySQL **8.0.16 or newer** (the schema uses CHECK constraints)
- Node.js 18 or newer, and npm

## First-time setup

### 1. Create the database

From the project folder:

```
mysql -u root -p < backend/db/schema.sql
mysql -u root -p < backend/db/seed.sql
```

`schema.sql` creates the `little_birds_sms` database and its tables. `seed.sql` loads the subjects and curriculum (it is safe to run again). No users are created here.

### 2. Start the backend

The backend reads its settings from environment variables. These two are **required**:

| Variable | Meaning |
|---|---|
| `JWT_SECRET` | Secret used to sign login tokens. At least 32 characters. The backend will not start without it. |
| `DB_PASSWORD` | Your MySQL password (the default is empty) |

These are used **only the first time**, when there are no staff accounts, to create the first Headmaster. Without them nobody can log in:

| Variable | Meaning |
|---|---|
| `SMS_ADMIN_USERNAME` | Headmaster username |
| `SMS_ADMIN_PASSWORD` | Headmaster password, at least 8 characters |

Optional: `DB_URL`, `DB_USER`, `SERVER_PORT` (default 8080), `JWT_EXPIRY_MINUTES` (default 480), `SMS_ADMIN_ID` (default ST001), `SMS_ADMIN_NAME`.

macOS / Linux:

```
cd backend
export JWT_SECRET='change-this-to-a-long-random-text-32-chars-or-more'
export DB_PASSWORD='your-mysql-password'
export SMS_ADMIN_USERNAME='headmaster'
export SMS_ADMIN_PASSWORD='choose-a-password'
mvn spring-boot:run
```

Windows PowerShell:

```
cd backend
$env:JWT_SECRET = 'change-this-to-a-long-random-text-32-chars-or-more'
$env:DB_PASSWORD = 'your-mysql-password'
$env:SMS_ADMIN_USERNAME = 'headmaster'
$env:SMS_ADMIN_PASSWORD = 'choose-a-password'
mvn spring-boot:run
```

When it is ready, http://localhost:8080/api/health answers. The log shows `Created the first HEADMASTER account` the first time.

### 3. Start the frontend

In a second terminal:

```
cd frontend
npm install
npm run dev
```

Open http://localhost:5173 and log in with `SMS_ADMIN_USERNAME` / `SMS_ADMIN_PASSWORD`. Vite forwards `/api` to the backend, so no CORS setup is needed. If the backend is not on port 8080, see `frontend/.env.example`.

### 4. Add the rest of the school

Log in as Headmaster, open **Staff** to create Subject, Working and Management staff accounts, then add Teachers and Students.

## Later runs

Set the environment variables again, then `mvn spring-boot:run` and `npm run dev`. The Headmaster variables are ignored once any staff account exists.

## Tests

```
cd backend
mvn test
```

These cover validation rules, grade and pass calculation, and the permission table. There are no automated frontend tests.

## Production build of the frontend

```
cd frontend
npm run build
```

The files appear in `frontend/dist/`. They must be served from the same address as `/api` (for example behind one web server that forwards `/api` to the backend), because the app calls `/api/...` with no host.

## Troubleshooting

| Problem | What to check |
|---|---|
| Backend stops at start with `sms.jwt.secret` or `JWT_SECRET must be at least 32 characters` | Set `JWT_SECRET` (32+ characters) |
| `Access denied for user` / cannot connect | `DB_PASSWORD`, `DB_USER`, `DB_URL`; is MySQL running? |
| `Schema-validation: missing table / wrong column type` | Run `schema.sql` on a fresh database. Hibernate only checks the tables, it does not create them. |
| Log says there are no staff accounts | Set `SMS_ADMIN_USERNAME` and `SMS_ADMIN_PASSWORD` (8+ characters) and restart |
| Login page: "Cannot reach the server" | Backend is not running, or is not on port 8080 |
| "Unknown subject" when saving staff | Tick subjects from the list; names must match the curriculum (for example `Mathematics`) |
| Forgot every Headmaster password | Delete all rows in `staff_users`, set the two `SMS_ADMIN_*` variables and restart |
