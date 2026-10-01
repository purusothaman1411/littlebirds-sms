import { useEffect, useState } from 'react';
import ResultsTable from '../components/ResultsTable.jsx';
import StudentMarks from '../components/StudentMarks.jsx';
import { usePermissions } from '../hooks/usePermissions.js';
import { getAbove, getHighest, getPassed, getRanking } from '../services/marksService.js';
import { getStudent, listStudents } from '../services/studentService.js';
import { listAllSubjects } from '../services/subjectService.js';

export default function Marks() {
  const { has } = usePermissions();
  const canEdit = has('MARKS_ADD') || has('MARKS_UPDATE');
  const [tab, setTab] = useState('student');

  return (
    <div>
      <h1>Marks</h1>
      <div className="tabs" role="tablist">
        <button role="tab" aria-selected={tab === 'student'} className={`tab ${tab === 'student' ? 'active' : ''}`} onClick={() => setTab('student')}>
          Student marks
        </button>
        <button role="tab" aria-selected={tab === 'class'} className={`tab ${tab === 'class' ? 'active' : ''}`} onClick={() => setTab('class')}>
          Rankings and results
        </button>
      </div>
      {tab === 'student' ? <StudentTab canEdit={canEdit} /> : <ClassTab />}
    </div>
  );
}

// ---------------------------------------------------------------- one student

function StudentTab({ canEdit }) {
  const [query, setQuery] = useState('');
  const [matches, setMatches] = useState(null); // null = not searched yet
  const [searching, setSearching] = useState(false);
  const [error, setError] = useState('');
  const [selected, setSelected] = useState(null); // student id

  const search = async (e) => {
    e.preventDefault();
    const q = query.trim();
    if (!q) return;
    setSearching(true);
    setError('');
    setSelected(null);
    try {
      // an exact ID wins; otherwise search by name
      const byId = await getStudent(q).catch(() => null);
      if (byId) {
        setMatches([byId]);
      } else {
        const page = await listStudents({ name: q, size: 10 });
        setMatches(page.content);
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setSearching(false);
    }
  };

  return (
    <div>
      <form className="toolbar" onSubmit={search}>
        <input
          type="search" placeholder="Student ID or name…" value={query}
          onChange={(e) => setQuery(e.target.value)} aria-label="Student ID or name"
        />
        <button type="submit" className="btn btn-secondary" disabled={searching}>
          {searching ? 'Searching…' : 'Find student'}
        </button>
      </form>

      {error && <div className="alert alert-error" role="alert">{error}</div>}

      {matches && matches.length === 0 && <p className="muted">No student found.</p>}
      {matches && matches.length > 0 && (
        <div className="match-list">
          {matches.map((s) => (
            <button
              key={s.studentId}
              className={`btn ${selected === s.studentId ? 'btn-primary btn-inline' : 'btn-secondary'}`}
              onClick={() => setSelected(s.studentId)}
            >
              {s.studentId} · {s.name} · Std {s.standard}
            </button>
          ))}
        </div>
      )}

      {selected && <StudentMarks studentId={selected} canEdit={canEdit} />}
      {!matches && <p className="muted">Search for a student to view{canEdit ? ' or enter' : ''} marks.</p>}
    </div>
  );
}

// ---------------------------------------------------------------- class views

const VIEWS = [
  { key: 'ranking', label: 'Ranking' },
  { key: 'passed', label: 'Passed students' },
  { key: 'above', label: 'Above a percentage' },
  { key: 'highest', label: 'Highest in a subject' },
];

function ClassTab() {
  const [view, setView] = useState('ranking');
  const [percent, setPercent] = useState('50');
  const [subject, setSubject] = useState('');
  const [rows, setRows] = useState(null);
  const [highest, setHighest] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [subjectNames, setSubjectNames] = useState([]);

  useEffect(() => {
    listAllSubjects().then(setSubjectNames).catch(() => setSubjectNames([]));
  }, []);

  const pick = (key) => {
    setView(key);
    setRows(null);
    setHighest(null);
    setError('');
  };

  const run = async (e) => {
    e?.preventDefault();
    setError('');
    setRows(null);
    setHighest(null);

    if (view === 'above' && (percent.trim() === '' || Number.isNaN(Number(percent)))) {
      setError('Enter a percentage, for example 50.');
      return;
    }
    if (view === 'highest' && !subject.trim()) {
      setError('Choose a subject.');
      return;
    }

    setLoading(true);
    try {
      if (view === 'ranking') setRows(await getRanking());
      else if (view === 'passed') setRows(await getPassed());
      else if (view === 'above') setRows(await getAbove(Number(percent)));
      else setHighest(await getHighest(subject.trim()));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="match-list">
        {VIEWS.map((v) => (
          <button key={v.key} className={`btn ${view === v.key ? 'btn-primary btn-inline' : 'btn-secondary'}`} onClick={() => pick(v.key)}>
            {v.label}
          </button>
        ))}
      </div>

      <form className="toolbar" onSubmit={run}>
        {view === 'above' && (
          <input type="number" min="0" max="100" value={percent} onChange={(e) => setPercent(e.target.value)} aria-label="Percentage" />
        )}
        {view === 'highest' && (
          <select value={subject} onChange={(e) => setSubject(e.target.value)} aria-label="Subject">
            <option value="">Choose subject…</option>
            {subjectNames.map((name) => <option key={name} value={name}>{name}</option>)}
          </select>
        )}
        <button type="submit" className="btn btn-primary btn-inline" disabled={loading}>
          {loading ? 'Loading…' : 'Show'}
        </button>
      </form>

      {view === 'ranking' && <p className="muted">All students by total marks entered, highest first.</p>}
      {view === 'passed' && <p className="muted">Students with all subjects entered and every mark at least 35.</p>}
      {view === 'above' && <p className="muted">Students with all subjects entered and a percentage above the value.</p>}

      {error && <div className="alert alert-error" role="alert">{error}</div>}

      {highest && (
        <div className="card">
          <div className="muted">Highest mark in {highest.subject}</div>
          <div className="stat-value">{highest.mark}</div>
          <div>{highest.studentName} ({highest.studentId})</div>
        </div>
      )}

      {rows && (
        <div className="card table-card">
          <ResultsTable rows={rows} ranked={view === 'ranking'} />
        </div>
      )}
    </div>
  );
}
