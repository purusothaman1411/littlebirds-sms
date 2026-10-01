import { useState } from 'react';
import {
  getAttendanceByDate, listClassStudents, markAttendance, todayISO,
} from '../services/attendanceService.js';

/** Mark a whole class for one date. Shows what is already saved for that date so it can be corrected. */
export default function MarkAttendance() {
  const [date, setDate] = useState(todayISO());
  const [standard, setStandard] = useState('');
  const [students, setStudents] = useState(null); // loaded class
  const [status, setStatus] = useState({}); // studentId -> 'P' | 'A'
  const [alreadySaved, setAlreadySaved] = useState(false);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const resetLoaded = () => {
    setStudents(null);
    setNotice('');
    setError('');
  };

  const load = async (e) => {
    e.preventDefault();
    resetLoaded();
    if (!date) return setError('Choose a date.');
    if (!standard) return setError('Choose a standard.');
    setLoading(true);
    try {
      const [list, existing] = await Promise.all([
        listClassStudents(Number(standard)),
        getAttendanceByDate(date, Number(standard)),
      ]);
      if (list.length === 0) {
        setError(`There are no students in standard ${standard}.`);
        return;
      }
      const saved = {};
      existing.forEach((r) => { saved[r.studentId.toLowerCase()] = r.status; });
      const next = {};
      list.forEach((s) => { next[s.studentId] = saved[s.studentId.toLowerCase()] ?? 'P'; });
      setAlreadySaved(existing.length > 0);
      setStatus(next);
      setStudents(list);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const setAll = (value) =>
    setStatus(Object.fromEntries(students.map((s) => [s.studentId, value])));

  const save = async () => {
    setSaving(true);
    setError('');
    setNotice('');
    try {
      const entries = students.map((s) => ({ studentId: s.studentId, status: status[s.studentId] }));
      const r = await markAttendance(date, Number(standard), entries);
      setAlreadySaved(true);
      setNotice(`Saved for ${r.date}: ${r.present} present, ${r.absent} absent.`);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  const absentCount = students ? students.filter((s) => status[s.studentId] === 'A').length : 0;

  return (
    <div>
      <form className="toolbar" onSubmit={load}>
        <input type="date" max={todayISO()} value={date}
          onChange={(e) => { setDate(e.target.value); resetLoaded(); }} aria-label="Date" />
        <select value={standard} onChange={(e) => { setStandard(e.target.value); resetLoaded(); }} aria-label="Standard">
          <option value="">Standard…</option>
          {Array.from({ length: 12 }, (_, i) => i + 1).map((n) => <option key={n} value={n}>Standard {n}</option>)}
        </select>
        <button type="submit" className="btn btn-primary btn-inline" disabled={loading}>
          {loading ? 'Loading…' : 'Load class'}
        </button>
      </form>

      {notice && <div className="alert alert-success" role="status">{notice}</div>}
      {error && <div className="alert alert-error" role="alert">{error}</div>}

      {students && (
        <>
          {alreadySaved && (
            <div className="alert alert-info">
              Attendance was already marked for this class on this date. Saving again will update it.
            </div>
          )}
          <div className="match-list">
            <button type="button" className="btn btn-secondary" onClick={() => setAll('P')}>All present</button>
            <button type="button" className="btn btn-secondary" onClick={() => setAll('A')}>All absent</button>
            <span className="muted center-v">
              {students.length - absentCount} present · {absentCount} absent
            </span>
          </div>

          <div className="card table-card">
            <div className="table-scroll">
              <table className="table">
                <thead><tr><th>ID</th><th>Name</th><th>Attendance</th></tr></thead>
                <tbody>
                  {students.map((s) => (
                    <tr key={s.studentId}>
                      <td>{s.studentId}</td>
                      <td>{s.name}</td>
                      <td>
                        <div className="seg" role="radiogroup" aria-label={`Attendance for ${s.name}`}>
                          <button type="button" role="radio" aria-checked={status[s.studentId] === 'P'}
                            className={`seg-btn ${status[s.studentId] === 'P' ? 'on-present' : ''}`}
                            onClick={() => setStatus((p) => ({ ...p, [s.studentId]: 'P' }))}>Present</button>
                          <button type="button" role="radio" aria-checked={status[s.studentId] === 'A'}
                            className={`seg-btn ${status[s.studentId] === 'A' ? 'on-absent' : ''}`}
                            onClick={() => setStatus((p) => ({ ...p, [s.studentId]: 'A' }))}>Absent</button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          <div className="form-actions">
            <button className="btn btn-primary btn-inline" onClick={save} disabled={saving}>
              {saving ? 'Saving…' : 'Save attendance'}
            </button>
          </div>
        </>
      )}
    </div>
  );
}
