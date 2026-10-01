import { useEffect, useState } from 'react';
import { GROUPS } from '../services/studentService.js';
import { CLASS_RANGES, ROLES } from '../services/staffService.js';
import { listAllSubjects } from '../services/subjectService.js';

const EMPTY = {
  staffId: '', name: '', username: '', password: '', role: '', classRange: 'ALL', group: '', subjects: [],
};

/**
 * Add / edit form. `staff` present = edit mode (ID cannot be changed; blank password keeps the current one).
 * onSubmit(payload) returns a promise and throws ApiError on failure.
 */
export default function StaffForm({ staff, onSubmit, onCancel }) {
  const editing = Boolean(staff);
  const [form, setForm] = useState(
    editing
      ? { ...EMPTY, ...staff, password: '', group: staff.group ?? '', classRange: staff.classRange || 'ALL', subjects: [...staff.subjects] }
      : EMPTY
  );
  const [fieldErrors, setFieldErrors] = useState({});
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [allSubjects, setAllSubjects] = useState([]);

  useEffect(() => {
    listAllSubjects().then(setAllSubjects).catch(() => setAllSubjects([]));
  }, []);

  const toggleSubject = (name) =>
    setForm((f) => ({
      ...f,
      subjects: f.subjects.includes(name) ? f.subjects.filter((x) => x !== name) : [...f.subjects, name],
    }));

  const set = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }));

  const errorFor = (key) =>
    fieldErrors[key] ||
    (key === 'subjects' ? Object.entries(fieldErrors).find(([k]) => k.startsWith('subjects'))?.[1] : undefined);
  const bad = (key) => (errorFor(key) ? 'true' : undefined);

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setFieldErrors({});
    setSaving(true);
    try {
      await onSubmit({
        ...form,
        staffId: form.staffId.trim(),
        name: form.name.trim(),
        username: form.username.trim(),
        group: form.group || null,
      });
    } catch (err) {
      setFieldErrors(err.errors || {});
      setError(Object.keys(err.errors || {}).length ? 'Please fix the highlighted fields.' : err.message);
      setSaving(false);
    }
  };

  const field = (key, label, input, hint) => (
    <div>
      <label htmlFor={`sf-${key}`}>{label}</label>
      {input}
      {hint && !errorFor(key) && <div className="muted hint">{hint}</div>}
      {errorFor(key) && <div className="field-error">{errorFor(key)}</div>}
    </div>
  );

  return (
    <form onSubmit={submit} noValidate>
      {error && <div className="alert alert-error" role="alert">{error}</div>}
      <div className="form-grid">
        {field('staffId', 'Staff ID',
          <input id="sf-staffId" value={form.staffId} onChange={set('staffId')} disabled={editing}
            aria-invalid={bad('staffId')} placeholder="e.g. ST101" />)}
        {field('name', 'Name',
          <input id="sf-name" value={form.name} onChange={set('name')} aria-invalid={bad('name')} />)}
        {field('username', 'Username',
          <input id="sf-username" value={form.username} onChange={set('username')} autoComplete="off"
            aria-invalid={bad('username')} />)}
        {field('password', editing ? 'New password' : 'Password',
          <div className="password-row">
            <input id="sf-password" type={showPassword ? 'text' : 'password'} value={form.password}
              onChange={set('password')} autoComplete="new-password" aria-invalid={bad('password')} />
            <button type="button" className="btn btn-secondary" onClick={() => setShowPassword((v) => !v)}>
              {showPassword ? 'Hide' : 'Show'}
            </button>
          </div>,
          editing ? 'Leave blank to keep the current password' : '8 to 72 characters')}
        {field('role', 'Role',
          <select id="sf-role" value={form.role} onChange={set('role')} aria-invalid={bad('role')}>
            <option value="">Select…</option>
            {ROLES.map((r) => <option key={r.value} value={r.value}>{r.label}</option>)}
          </select>)}
        {field('classRange', 'Class range',
          <select id="sf-classRange" value={form.classRange} onChange={set('classRange')} aria-invalid={bad('classRange')}>
            {CLASS_RANGES.map((c) => <option key={c} value={c}>{c}</option>)}
          </select>)}
        {field('group', 'Group',
          <select id="sf-group" value={form.group} onChange={set('group')} aria-invalid={bad('group')}>
            <option value="">None</option>
            {GROUPS.map((g) => <option key={g} value={g}>{g}</option>)}
          </select>)}
      </div>
      <div>
        <label>Subjects</label>
        <div className="check-grid">
          {allSubjects.map((name) => (
            <label key={name} className="check-item">
              <input type="checkbox" checked={form.subjects.includes(name)} onChange={() => toggleSubject(name)} />
              {name}
            </label>
          ))}
        </div>
        {errorFor('subjects')
          ? <div className="field-error">{errorFor('subjects')}</div>
          : <div className="muted hint">Tick the subjects this staff member teaches. Leave all unticked for no subject restriction.</div>}
      </div>

      <div className="form-actions">
        <button type="button" className="btn btn-secondary" onClick={onCancel} disabled={saving}>Cancel</button>
        <button type="submit" className="btn btn-primary btn-inline" disabled={saving}>
          {saving ? 'Saving…' : editing ? 'Save changes' : 'Add staff'}
        </button>
      </div>
    </form>
  );
}
