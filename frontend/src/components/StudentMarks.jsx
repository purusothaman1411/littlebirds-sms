import { useEffect, useState } from 'react';
import ResultBadge from './ResultBadge.jsx';
import { getStudentMarks, saveStudentMarks } from '../services/marksService.js';

/** Marks of one student: summary, and (if allowed) an editable table for every curriculum subject. */
export default function StudentMarks({ studentId, canEdit }) {
  const [data, setData] = useState(null);
  const [inputs, setInputs] = useState({}); // subject -> text in the box
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [saving, setSaving] = useState(false);

  const show = (response) => {
    setData(response);
    const next = {};
    response.marks.forEach((m) => { next[m.subject] = String(m.mark); });
    response.missingSubjects.forEach((s) => { next[s] = ''; });
    setInputs(next);
  };

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError('');
    setNotice('');
    getStudentMarks(studentId)
      .then((r) => { if (!cancelled) show(r); })
      .catch((err) => { if (!cancelled) { setData(null); setError(err.message); } })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [studentId]);

  if (loading) return <p className="muted">Loading marks…</p>;
  if (!data) return error ? <div className="alert alert-error" role="alert">{error}</div> : null;

  const saved = {};
  data.marks.forEach((m) => { saved[m.subject] = String(m.mark); });
  const subjects = [...data.marks.map((m) => m.subject), ...data.missingSubjects];
  const changed = subjects.filter((s) => inputs[s].trim() !== (saved[s] ?? ''));

  const save = async (e) => {
    e.preventDefault();
    setError('');
    setNotice('');
    // only the changed subjects are sent, so a subject teacher is not blocked by subjects that are not theirs
    const payload = {};
    for (const subject of changed) {
      const text = inputs[subject].trim();
      if (text === '') {
        setError(`Enter a mark for ${subject}, or put back the old value. A mark cannot be removed.`);
        return;
      }
      const value = Number(text);
      if (!Number.isInteger(value) || value < 0 || value > 100) {
        setError(`${subject}: enter a whole number between 0 and 100.`);
        return;
      }
      payload[subject] = value;
    }
    setSaving(true);
    try {
      show(await saveStudentMarks(studentId, payload));
      setNotice('Marks saved.');
    } catch (err) {
      const details = Object.values(err.errors || {});
      setError(details.length ? details.join(' ') : err.message);
    } finally {
      setSaving(false);
    }
  };

  const s = data.summary;

  return (
    <div className="card">
      <h2 className="no-top">
        {data.studentName} <span className="muted">({data.studentId})</span>
      </h2>
      <p className="muted">
        Standard {data.standard}{data.group ? ` · ${data.group}` : ''}
      </p>

      <div className="summary-row">
        <div><div className="muted small">Total</div><strong>{s.total} / {s.maxTotal}</strong></div>
        <div><div className="muted small">Percentage</div><strong>{s.percentage != null ? `${s.percentage}%` : '–'}</strong></div>
        <div><div className="muted small">Grade</div><strong>{s.grade ?? '–'}</strong></div>
        <div><div className="muted small">Result</div><ResultBadge result={s.result} /></div>
      </div>
      {!s.complete && (
        <p className="muted">Percentage, grade and pass/fail are shown once marks for all subjects are entered.</p>
      )}

      {notice && <div className="alert alert-success" role="status">{notice}</div>}
      {error && <div className="alert alert-error" role="alert">{error}</div>}

      <form onSubmit={save} noValidate>
        <table className="table">
          <thead><tr><th>Subject</th><th>Mark (0-100)</th></tr></thead>
          <tbody>
            {subjects.map((subject) => (
              <tr key={subject}>
                <td>{subject}</td>
                <td>
                  {canEdit ? (
                    <input
                      className="mark-input" inputMode="numeric" value={inputs[subject]}
                      onChange={(e) => setInputs((prev) => ({ ...prev, [subject]: e.target.value }))}
                      aria-label={`${subject} mark`} placeholder="–"
                    />
                  ) : saved[subject] ?? <span className="muted">Not entered</span>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {canEdit && (
          <div className="form-actions">
            <button type="submit" className="btn btn-primary btn-inline" disabled={saving || changed.length === 0}>
              {saving ? 'Saving…' : 'Save marks'}
            </button>
          </div>
        )}
      </form>
    </div>
  );
}
