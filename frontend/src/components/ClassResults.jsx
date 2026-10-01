import { useState } from 'react';
import ResultsTable from './ResultsTable.jsx';
import { getClassResults } from '../services/reportService.js';

const sortByTotal = (rows) =>
  [...rows].sort((a, b) => b.total - a.total || a.studentId.localeCompare(b.studentId, undefined, { sensitivity: 'base' }));

function stats(rows) {
  const complete = rows.filter((r) => r.complete);
  const passed = rows.filter((r) => r.result === 'PASS').length;
  const failed = rows.filter((r) => r.result === 'FAIL').length;
  const average = complete.length
    ? Math.round((complete.reduce((sum, r) => sum + r.percentage, 0) / complete.length) * 100) / 100
    : null;
  return { total: rows.length, passed, failed, incomplete: rows.length - complete.length, average };
}

export default function ClassResults() {
  const [standard, setStandard] = useState('');
  const [classes, setClasses] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const run = async (e) => {
    e.preventDefault();
    setError('');
    setClasses(null);
    setLoading(true);
    try {
      setClasses(await getClassResults(standard ? Number(standard) : undefined));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <form className="toolbar no-print" onSubmit={run}>
        <select value={standard} onChange={(e) => setStandard(e.target.value)} aria-label="Standard">
          <option value="">All classes</option>
          {Array.from({ length: 12 }, (_, i) => i + 1).map((n) => <option key={n} value={n}>Standard {n}</option>)}
        </select>
        <button type="submit" className="btn btn-primary btn-inline" disabled={loading}>
          {loading ? 'Loading…' : 'Show results'}
        </button>
        {classes && classes.length > 0 && (
          <button type="button" className="btn btn-secondary" onClick={() => window.print()}>Print</button>
        )}
      </form>

      {error && <div className="alert alert-error" role="alert">{error}</div>}
      {classes && classes.length === 0 && <p className="muted">No students found.</p>}

      {classes && classes.map((c) => {
        const st = stats(c.students);
        return (
          <section key={c.standard} className="class-block">
            <h2>Standard {c.standard}</h2>
            <p className="muted">
              {st.total} students · {st.passed} passed · {st.failed} failed · {st.incomplete} incomplete
              {st.average != null && ` · class average ${st.average}%`}
            </p>
            <div className="card table-card">
              <ResultsTable rows={sortByTotal(c.students)} ranked />
            </div>
          </section>
        );
      })}
    </div>
  );
}
