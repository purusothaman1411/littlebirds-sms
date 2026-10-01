// One list drives the sidebar and the dashboard's quick links. `permission` / `anyOf` match the routes in AppRoutes.
export const NAV_ITEMS = [
  { to: '/', label: 'Dashboard', end: true },
  { to: '/students', label: 'Students', permission: 'STUDENT_VIEW', description: 'Admissions, details and classes' },
  { to: '/teachers', label: 'Teachers', permission: 'TEACHER_VIEW', description: 'Teacher records and subjects' },
  { to: '/staff', label: 'Staff', permission: 'STAFF_MANAGE', description: 'Login accounts and roles' },
  { to: '/marks', label: 'Marks', permission: 'MARKS_VIEW', description: 'Enter and review marks' },
  { to: '/attendance', label: 'Attendance', permission: 'ATTENDANCE_VIEW', description: 'Mark and view daily attendance' },
  {
    to: '/reports',
    label: 'Reports',
    anyOf: ['REPORT_CLASS_VIEW', 'REPORT_CARD_VIEW'],
    description: 'Report cards and class results',
  },
];
