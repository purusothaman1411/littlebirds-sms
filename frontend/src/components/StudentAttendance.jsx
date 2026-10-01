import { useEffect, useState } from 'react';
import StudentPicker from './StudentPicker.jsx';
import { getStudentAttendance } from '../services/attendanceService.js';

export default function StudentAttendance() {
  const [studentId, setStudentId] = useState(null);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!studentId) {
      setData(null);
      return undefined;
    }
    let cancelled = false;
    setLoading(true);
    setError('');
    getStudentAttendance(studentId)
      .then((r) => { if (!cancelled) setData(r); })
      .catch((err) => { if (!cancelled) { setData(null); setError(err.message); } })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [studentId]);

  return (
    <div>
      <StudentPicker onSelect={setStudentId} />
      {loading && <p className="muted">Loading attendance…</p>}
      {error && <div className="alert alert-error" role="alert">{error}</div>}

      {data && (
        <div className="card">
          <h2 className="no-top">{data.studentName} <span className="muted">({data.studentId})</span></h2>
          <div className="summary-row">
            <div><div className="muted small">Attendance</div><strong>{data.summary.percentage}%</strong></div>
            <div><div className="muted small">Present days</div><strong>{data.summary.presentDays}</strong></div>
            <div><div className="muted small">Absent days</div><strong>{data.summary.absentDays}</strong></div>
            <div><div className="muted small">Total days</div><strong>{data.summary.totalDays}</strong></div>
          </div>
          {data.records.length === 0 ? (
            <p className="muted">No attendance has been marked for this student yet.</p>
          ) : (
            <table className="table">
              <thead><tr><th>Date</th><th>Status</th></tr></thead>
              <tbody>
                {[...data.records].reverse().map((r) => (
                  <tr key={r.date}>
                    <td>{r.date}</td>
                    <td>
                      <span className={`badge ${r.status === 'P' ? 'badge-pass' : 'badge-fail'}`}>
                        {r.status === 'P' ? 'Present' : 'Absent'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  );
}
