# Little Birds SMS: Frontend

React 18 + React Router, plain JavaScript, built with Vite. See the main `../README.md` for full setup.

## Run (development)

1. Start the backend first (see `../backend`), on port 8080.
2. In this folder:
   ```
   npm install
   npm run dev
   ```
3. Open http://localhost:5173 and log in with the Headmaster account you set in `SMS_ADMIN_USERNAME` / `SMS_ADMIN_PASSWORD`.

Vite forwards every `/api/...` request to the backend, so there are no CORS settings to change.
If the backend is not on `http://localhost:8080`, set `VITE_API_PROXY` (see `.env.example`).

## Build

`npm run build` writes `dist/`. Serve it from the same address as `/api`.

## Structure

```
src/
  services/   api.js (the only fetch call) + one file per module
              (auth, dashboard, student, teacher, staff, marks, attendance, report, subject)
  context/    AuthContext.jsx (user, login, logout, session restore)
  hooks/      usePermissions.js (show/hide only; the backend enforces)
  routes/     AppRoutes.jsx, ProtectedRoute.jsx
  components/ Layout, Sidebar, Topbar, navItems, Modal, forms, tables, shared pieces
  pages/      Login, Dashboard, Students, Teachers, Staff, Marks, Attendance, Reports,
              Unauthorized, NotFound
```

The login session is kept in `sessionStorage`, so closing the tab logs out.
