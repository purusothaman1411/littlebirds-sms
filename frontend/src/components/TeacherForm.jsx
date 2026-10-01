import { useState } from 'react';
import { GENDERS } from '../services/studentService.js';

const EMPTY = {
  teacherId: '', name: '', dob: '', gender: '', qualification: '', email: '', contactNumber: '', address: '', subjects: '',
};

// Subjects are typed as a comma-separated list: "Maths, Physics"
const parseSubjects = (text) => text.split(',').map((s) => s.trim()).filter(Boolean);

/** Add / edit form. `teacher` present = edit mode (ID cannot be changed). onSubmit(payload) returns a promise and throws ApiError on failure. */
export default function TeacherForm({ teacher, onSubmit, onCancel }) {
  const editing = Boolean(teacher);
  const [form, setForm] = useState(
    editing ? { ...EMPTY, ...teacher, subjects: teacher.subjects.join(', ') } : EMPTY
  );
  const [fieldErrors, setFieldErrors] = useState({});
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const today = new Date().toISOString().slice(0, 10);
  const set = (key) => (e) => setForm((f) => ({ ...f, [key]: e.target.value }));

  // a bad subject item comes back as "subjects[0]"; show it under the subjects field
  const errorFor = (key) =>
    fieldErrors[key] ||
    (key === 'subjects' ? Object.entries(fieldErrors).find(([k]) => k.startsWith('subjects'))?.[1] : undefined);

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setFieldErrors({});
    setSaving(true);
    try {
      await onSubmit({
        ...form,
        teacherId: form.teacherId.trim(),
        name: form.name.trim(),
        qualification: form.qualification.trim(),
        email: form.email.trim(),
        contactNumber: form.contactNumber.trim(),
        address: form.address.trim(),
        subjects: parseSubjects(form.subjects),
      });
    } catch (err) {
      setFieldErrors(err.errors || {});
      setError(Object.keys(err.errors || {}).length ? 'Please fix the highlighted fields.' : err.message);
      setSaving(false);
    }
  };

  const field = (key, label, input, hint) => (
    <div>
      <label htmlFor={`tf-${key}`}>{label}</label>
      {input}
      {hint && !errorFor(key) && <div className="muted hint">{hint}</div>}
      {errorFor(key) && <div className="field-error">{errorFor(key)}</div>}
    </div>
  );
  const bad = (key) => (errorFor(key) ? 'true' : undefined);

  return (
    <form onSubmit={submit} noValidate>
      {error && <div className="alert alert-error" role="alert">{error}</div>}
      <div className="form-grid">
        {field('teacherId', 'Teacher ID',
          <input id="tf-teacherId" value={form.teacherId} onChange={set('teacherId')} disabled={editing}
            aria-invalid={bad('teacherId')} placeholder="e.g. T101" />)}
        {field('name', 'Name',
          <input id="tf-name" value={form.name} onChange={set('name')} aria-invalid={bad('name')} />)}
        {field('dob', 'Date of birth',
          <input id="tf-dob" type="date" max={today} value={form.dob} onChange={set('dob')} aria-invalid={bad('dob')} />)}
        {field('gender', 'Gender',
          <select id="tf-gender" value={form.gender} onChange={set('gender')} aria-invalid={bad('gender')}>
            <option value="">Select…</option>
            {GENDERS.map((g) => <option key={g} value={g}>{g}</option>)}
          </select>)}
        {field('qualification', 'Qualification',
          <input id="tf-qualification" value={form.qualification} onChange={set('qualification')} aria-invalid={bad('qualification')} />)}
        {field('email', 'Email',
          <input id="tf-email" type="email" value={form.email} onChange={set('email')} aria-invalid={bad('email')} />)}
        {field('contactNumber', 'Contact number',
          <input id="tf-contactNumber" inputMode="numeric" maxLength={10} value={form.contactNumber}
            onChange={set('contactNumber')} aria-invalid={bad('contactNumber')} />)}
      </div>
      {field('address', 'Address',
        <input id="tf-address" value={form.address} onChange={set('address')} aria-invalid={bad('address')} />)}
      {field('subjects', 'Subjects',
        <input id="tf-subjects" value={form.subjects} onChange={set('subjects')} aria-invalid={bad('subjects')}
          placeholder="Maths, Physics" />,
        'Separate subjects with commas')}

      <div className="form-actions">
        <button type="button" className="btn btn-secondary" onClick={onCancel} disabled={saving}>Cancel</button>
        <button type="submit" className="btn btn-primary btn-inline" disabled={saving}>
          {saving ? 'Saving…' : editing ? 'Save changes' : 'Add teacher'}
        </button>
      </div>
    </form>
  );
}
