import { useState } from 'react';
import { getStudent, listStudents } from '../services/studentService.js';

/** Search by exact ID or part of a name; calls onSelect(studentId) when one is clicked. */
export default function StudentPicker({ onSelect }) {
  const [query, setQuery] = useState('');
  const [matches, setMatches] = useState(null);
  const [selected, setSelected] = useState(null);
  const [searching, setSearching] = useState(false);
  const [error, setError] = useState('');

  const search = async (e) => {
    e.preventDefault();
    const q = query.trim();
    if (!q) return;
    setSearching(true);
    setError('');
    setSelected(null);
    onSelect(null);
    try {
      const byId = await getStudent(q).catch(() => null);
      setMatches(byId ? [byId] : (await listStudents({ name: q, size: 10 })).content);
    } catch (err) {
      setError(err.message);
    } finally {
      setSearching(false);
    }
  };

  const pick = (id) => {
    setSelected(id);
    onSelect(id);
  };

  return (
    <div>
      <form className="toolbar" onSubmit={search}>
        <input type="search" placeholder="Student ID or name…" value={query}
          onChange={(e) => setQuery(e.target.value)} aria-label="Student ID or name" />
        <button type="submit" className="btn btn-secondary" disabled={searching}>
          {searching ? 'Searching…' : 'Find student'}
        </button>
      </form>
      {error && <div className="alert alert-error" role="alert">{error}</div>}
      {matches && matches.length === 0 && <p className="muted">No student found.</p>}
      {matches && matches.length > 0 && (
        <div className="match-list">
          {matches.map((s) => (
            <button key={s.studentId} onClick={() => pick(s.studentId)}
              className={`btn ${selected === s.studentId ? 'btn-primary btn-inline' : 'btn-secondary'}`}>
              {s.studentId} · {s.name} · Std {s.standard}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
