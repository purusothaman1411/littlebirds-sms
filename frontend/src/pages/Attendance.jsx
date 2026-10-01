import { useState } from 'react';
import AttendanceByDate from '../components/AttendanceByDate.jsx';
import MarkAttendance from '../components/MarkAttendance.jsx';
import StudentAttendance from '../components/StudentAttendance.jsx';
import { usePermissions } from '../hooks/usePermissions.js';

export default function Attendance() {
  const { has } = usePermissions();
  const canMark = has('ATTENDANCE_MARK');

  const tabs = [
    ...(canMark ? [{ key: 'mark', label: 'Mark class' }] : []),
    { key: 'date', label: 'By date' },
    { key: 'student', label: 'By student' },
  ];
  const [tab, setTab] = useState(tabs[0].key);

  return (
    <div>
      <h1>Attendance</h1>
      <div className="tabs" role="tablist">
        {tabs.map((t) => (
          <button key={t.key} role="tab" aria-selected={tab === t.key}
            className={`tab ${tab === t.key ? 'active' : ''}`} onClick={() => setTab(t.key)}>
            {t.label}
          </button>
        ))}
      </div>
      {tab === 'mark' && <MarkAttendance />}
      {tab === 'date' && <AttendanceByDate />}
      {tab === 'student' && <StudentAttendance />}
    </div>
  );
}
