import { useState } from 'react';
import { GENDERS, GROUPS } from '../services/studentService.js';

const EMPTY = {
  studentId: '', name: '', dob: '', gender: '', standard: '', group: '', address: '', contactNumber: '',
};

/**
 * Add / edit form. `student` present = edit mode (ID cannot be changed).
 * onSubmit(payload) must return a promise; it should throw an ApiError on failure so field errors show here.
 */
export default function StudentForm({ student, onSubmit, onCancel }) {
  const editing = Boolean(student);
  const [form, setForm] = useState(
    editing ? { ...EMPTY, ...student, group: student.group ?? '', standard: String(student.standard) } : EMPTY
  );
  const [fieldErrors, setFieldErrors] = useState({});
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const needsGroup = Number(form.standard) >= 11;
  const today = new Date().toISOString().slice(0, 10);

  const set = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }));

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setFieldErrors({});
    setSaving(true);
    try {
      await onSubmit({
        ...form,
        studentId: form.studentId.trim(),
        name: form.name.trim(),
        standard: form.standard === '' ? null : Number(form.standard),
        group: needsGroup ? form.group : null,
        address: form.address.trim(),
        contactNumber: form.contactNumber.trim(),
      });
    } catch (err) {
      setFieldErrors(err.errors || {});
      setError(Object.keys(err.errors || {}).length ? 'Please fix the highlighted fields.' : err.message);
      setSaving(false);
    }
  };

  const field = (key, label, input) => (
    <div>
      <label htmlFor={`sf-${key}`}>{label}</label>
      {input}
      {fieldErrors[key] && <div className="field-error">{fieldErrors[key]}</div>}
    </div>
  );
  const bad = (key) => (fieldErrors[key] ? 'true' : undefined);

  return (
    <form onSubmit={submit} noValidate>
      {error && <div className="alert alert-error" role="alert">{error}</div>}
      <div className="form-grid">
        {field('studentId', 'Student ID',
          <input id="sf-studentId" value={form.studentId} onChange={set('studentId')} disabled={editing}
            aria-invalid={bad('studentId')} placeholder="e.g. S101" />)}
        {field('name', 'Name',
          <input id="sf-name" value={form.name} onChange={set('name')} aria-invalid={bad('name')} />)}
        {field('dob', 'Date of birth',
          <input id="sf-dob" type="date" max={today} value={form.dob} onChange={set('dob')} aria-invalid={bad('dob')} />)}
        {field('gender', 'Gender',
          <select id="sf-gender" value={form.gender} onChange={set('gender')} aria-invalid={bad('gender')}>
            <option value="">Select…</option>
            {GENDERS.map((g) => <option key={g} value={g}>{g}</option>)}
          </select>)}
        {field('standard', 'Standard',
          <select id="sf-standard" value={form.standard} onChange={set('standard')} aria-invalid={bad('standard')}>
            <option value="">Select…</option>
            {Array.from({ length: 12 }, (_, i) => i + 1).map((n) => <option key={n} value={n}>{n}</option>)}
          </select>)}
        {needsGroup && field('group', 'Group',
          <select id="sf-group" value={form.group} onChange={set('group')} aria-invalid={bad('group')}>
            <option value="">Select…</option>
            {GROUPS.map((g) => <option key={g} value={g}>{g}</option>)}
          </select>)}
        {field('contactNumber', 'Contact number',
          <input id="sf-contactNumber" inputMode="numeric" maxLength={10} value={form.contactNumber}
            onChange={set('contactNumber')} aria-invalid={bad('contactNumber')} />)}
      </div>
      {field('address', 'Address',
        <input id="sf-address" value={form.address} onChange={set('address')} aria-invalid={bad('address')} />)}

      <div className="form-actions">
        <button type="button" className="btn btn-secondary" onClick={onCancel} disabled={saving}>Cancel</button>
        <button type="submit" className="btn btn-primary btn-inline" disabled={saving}>
          {saving ? 'Saving…' : editing ? 'Save changes' : 'Add student'}
        </button>
      </div>
    </form>
  );
}
