import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from '../components/Layout.jsx';
import ProtectedRoute from './ProtectedRoute.jsx';
import Login from '../pages/Login.jsx';
import Dashboard from '../pages/Dashboard.jsx';
import Students from '../pages/Students.jsx';
import Teachers from '../pages/Teachers.jsx';
import Staff from '../pages/Staff.jsx';
import Marks from '../pages/Marks.jsx';
import Attendance from '../pages/Attendance.jsx';
import Reports from '../pages/Reports.jsx';
import Unauthorized from '../pages/Unauthorized.jsx';
import NotFound from '../pages/NotFound.jsx';

// Every module page below is built; each route is guarded by the permission(s) it needs.
export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route path="/" element={<Dashboard />} />
        <Route path="/unauthorized" element={<Unauthorized />} />

        <Route
          path="/students"
          element={
            <ProtectedRoute permission="STUDENT_VIEW">
              <Students />
            </ProtectedRoute>
          }
        />
        <Route
          path="/teachers"
          element={
            <ProtectedRoute permission="TEACHER_VIEW">
              <Teachers />
            </ProtectedRoute>
          }
        />
        <Route
          path="/staff"
          element={
            <ProtectedRoute permission="STAFF_MANAGE">
              <Staff />
            </ProtectedRoute>
          }
        />
        <Route
          path="/marks"
          element={
            <ProtectedRoute permission="MARKS_VIEW">
              <Marks />
            </ProtectedRoute>
          }
        />
        <Route
          path="/attendance"
          element={
            <ProtectedRoute permission="ATTENDANCE_VIEW">
              <Attendance />
            </ProtectedRoute>
          }
        />
        <Route
          path="/reports"
          element={
            <ProtectedRoute anyOf={['REPORT_CLASS_VIEW', 'REPORT_CARD_VIEW']}>
              <Reports />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<NotFound />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
