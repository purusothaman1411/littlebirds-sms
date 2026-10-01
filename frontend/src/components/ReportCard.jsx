import { useEffect, useState } from 'react';
import ResultBadge from './ResultBadge.jsx';
import StudentPicker from './StudentPicker.jsx';
import { getReportCard } from '../services/reportService.js';

export default function ReportCard() {
  const [studentId, setStudentId] = useState(null);
  const [card, setCard] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!studentId) {
      setCard(null);
      return undefined;
    }
    let cancelled = false;
    setLoading(true);
    setError('');
    getReportCard(studentId)
      .then((r) => { if (!cancelled) setCard(r); })
      .catch((err) => { if (!cancelled) { setCard(null); setError(err.message); } })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [studentId]);

  return (
    <div>
      <div className="no-print">
        <StudentPicker onSelect={setStudentId} />
      </div>
      {loading && <p className="muted">Loading report card…</p>}
      {error && <div className="alert alert-error" role="alert">{error}</div>}
      {card && <Card card={card} />}
    </div>
  );
}

function Card({ card }) {
  const { student, marks, attendance } = card;
  const s = marks.summary;
  const subjects = [
    ...marks.marks.map((m) => ({ subject: m.subject, mark: m.mark })),
    ...marks.missingSubjects.map((name) => ({ subject: name, mark: null })),
  ];

  return (
    <div className="card report-card">
      <div className="report-head">
        <div>
          <h2 className="no-top">Little Birds School</h2>
          <div className="muted">Progress report</div>
        </div>
        <button className="btn btn-secondary no-print" onClick={() => window.print()}>Print</button>
      </div>

      <dl className="details">
        <div><dt>Name</dt><dd>{student.name}</dd></div>
        <div><dt>Student ID</dt><dd>{student.studentId}</dd></div>
        <div><dt>Standard</dt><dd>{student.standard}{student.group ? ` · ${student.group}` : ''}</dd></div>
        <div><dt>Gender</dt><dd>{student.gender}</dd></div>
        <div><dt>Date of birth</dt><dd>{student.dob}</dd></div>
        <div><dt>Contact</dt><dd>{student.contactNumber}</dd></div>
      </dl>

      <table className="table">
        <thead><tr><th>Subject</th><th>Mark</th></tr></thead>
        <tbody>
          {subjects.map((row) => (
            <tr key={row.subject}>
              <td>{row.subject}</td>
              <td>{row.mark ?? <span className="muted">Not entered</span>}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="summary-row">
        <div><div className="muted small">Total</div><strong>{s.total} / {s.maxTotal}</strong></div>
        <div><div className="muted small">Percentage</div><strong>{s.percentage != null ? `${s.percentage}%` : '–'}</strong></div>
        <div><div className="muted small">Grade</div><strong>{s.grade ?? '–'}</strong></div>
        <div><div className="muted small">Result</div><ResultBadge result={s.result} /></div>
      </div>
      {!s.complete && (
        <p className="muted">Marks pending for: {marks.missingSubjects.join(', ')}.</p>
      )}

      <h3>Attendance</h3>
      {attendance.totalDays === 0 ? (
        <p className="muted">No attendance has been marked yet.</p>
      ) : (
        <div className="summary-row">
          <div><div className="muted small">Attendance</div><strong>{attendance.percentage}%</strong></div>
          <div><div className="muted small">Present days</div><strong>{attendance.presentDays}</strong></div>
          <div><div className="muted small">Absent days</div><strong>{attendance.absentDays}</strong></div>
          <div><div className="muted small">Total days</div><strong>{attendance.totalDays}</strong></div>
        </div>
      )}
    </div>
  );
}
