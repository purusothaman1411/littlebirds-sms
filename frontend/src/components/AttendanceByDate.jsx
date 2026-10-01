import { useState } from 'react';
import { getAttendanceByDate, todayISO } from '../services/attendanceService.js';

export default function AttendanceByDate() {
  const [date, setDate] = useState(todayISO());
  const [standard, setStandard] = useState('');
  const [rows, setRows] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const run = async (e) => {
    e.preventDefault();
    setError('');
    setRows(null);
    if (!date) return setError('Choose a date.');
    setLoading(true);
    try {
      setRows(await getAttendanceByDate(date, standard ? Number(standard) : undefined));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const absent = rows ? rows.filter((r) => r.status === 'A').length : 0;

  return (
    <div>
      <form className="toolbar" onSubmit={run}>
        <input type="date" value={date} onChange={(e) => setDate(e.target.value)} aria-label="Date" />
        <select value={standard} onChange={(e) => setStandard(e.target.value)} aria-label="Standard">
          <option value="">Whole school</option>
          {Array.from({ length: 12 }, (_, i) => i + 1).map((n) => <option key={n} value={n}>Standard {n}</option>)}
        </select>
        <button type="submit" className="btn btn-primary btn-inline" disabled={loading}>
          {loading ? 'Loading…' : 'Show'}
        </button>
      </form>

      {error && <div className="alert alert-error" role="alert">{error}</div>}

      {rows && (rows.length === 0 ? (
        <p className="muted">No attendance has been marked for this date.</p>
      ) : (
        <>
          <p className="muted">{rows.length - absent} present · {absent} absent</p>
          <div className="card table-card">
            <div className="table-scroll">
              <table className="table">
                <thead><tr><th>ID</th><th>Name</th><th>Status</th></tr></thead>
                <tbody>
                  {rows.map((r) => (
                    <tr key={r.studentId}>
                      <td>{r.studentId}</td>
                      <td>{r.studentName}</td>
                      <td>
                        <span className={`badge ${r.status === 'P' ? 'badge-pass' : 'badge-fail'}`}>
                          {r.status === 'P' ? 'Present' : 'Absent'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </>
      ))}
    </div>
  );
}
