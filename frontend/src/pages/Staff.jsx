import { useCallback, useEffect, useState } from 'react';
import Modal from '../components/Modal.jsx';
import StaffForm from '../components/StaffForm.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import {
  createStaff, deleteStaff, listStaff, resetStaffPassword, roleLabel, updateStaff,
} from '../services/staffService.js';

export default function Staff() {
  const { user } = useAuth();
  const [staff, setStaff] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [formFor, setFormFor] = useState(null); // null | 'new' | staff member
  const [toDelete, setToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);
  const [resetFor, setResetFor] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      setStaff(await listStaff());
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const save = async (payload) => {
    if (formFor === 'new') {
      await createStaff(payload);
      setNotice(`Staff ${payload.staffId} added.`);
    } else {
      await updateStaff(formFor.staffId, payload);
      setNotice(`Staff ${formFor.staffId} updated.`);
    }
    setFormFor(null);
    await load();
  };

  const confirmDelete = async () => {
    setDeleting(true);
    try {
      await deleteStaff(toDelete.staffId);
      setNotice(`Staff ${toDelete.staffId} deleted.`);
      setToDelete(null);
      await load();
    } catch (err) {
      setError(err.message);
      setToDelete(null);
    } finally {
      setDeleting(false);
    }
  };

  const assignment = (s) => {
    const parts = [s.classRange === 'ALL' ? 'All classes' : `Std ${s.classRange}`];
    if (s.group) parts.push(s.group);
    if (s.subjects.length) parts.push(s.subjects.join(', '));
    return parts.join(' · ');
  };

  return (
    <div>
      <div className="page-header">
        <h1>Staff accounts</h1>
        <button className="btn btn-primary btn-inline" onClick={() => { setNotice(''); setFormFor('new'); }}>
          + Add staff
        </button>
      </div>

      {notice && <div className="alert alert-success" role="status">{notice}</div>}
      {error && <div className="alert alert-error" role="alert">{error}</div>}

      <div className="card table-card">
        {loading && !staff ? (
          <p className="muted pad">Loading staff…</p>
        ) : staff && staff.length === 0 ? (
          <p className="muted pad">No staff accounts.</p>
        ) : (
          staff && (
            <div className="table-scroll">
              <table className="table">
                <thead>
                  <tr><th>ID</th><th>Name</th><th>Username</th><th>Role</th><th>Assignment</th><th></th></tr>
                </thead>
                <tbody>
                  {staff.map((s) => {
                    const isSelf = s.staffId.toLowerCase() === user.staffId?.toLowerCase();
                    return (
                      <tr key={s.staffId}>
                        <td>{s.staffId}</td>
                        <td>{s.name}{isSelf && <span className="muted"> (you)</span>}</td>
                        <td>{s.username}</td>
                        <td>{roleLabel(s.role)}</td>
                        <td className="wrap">{assignment(s)}</td>
                        <td className="row-actions">
                          <button className="btn btn-link" onClick={() => { setNotice(''); setFormFor(s); }}>Edit</button>
                          <button className="btn btn-link" onClick={() => { setNotice(''); setResetFor(s); }}>Reset password</button>
                          {!isSelf && <button className="btn btn-link danger" onClick={() => setToDelete(s)}>Delete</button>}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )
        )}
      </div>

      {formFor && (
        <Modal title={formFor === 'new' ? 'Add staff' : `Edit ${formFor.staffId}`} onClose={() => setFormFor(null)}>
          <StaffForm staff={formFor === 'new' ? null : formFor} onSubmit={save} onCancel={() => setFormFor(null)} />
        </Modal>
      )}

      {resetFor && (
        <ResetPasswordModal
          member={resetFor}
          onClose={() => setResetFor(null)}
          onDone={() => { setNotice(`Password for ${resetFor.staffId} changed.`); setResetFor(null); }}
        />
      )}

      {toDelete && (
        <Modal title="Delete staff account?" onClose={() => !deleting && setToDelete(null)}>
          <p>Delete <strong>{toDelete.name}</strong> ({toDelete.username})? They will no longer be able to log in.</p>
          <div className="form-actions">
            <button className="btn btn-secondary" onClick={() => setToDelete(null)} disabled={deleting}>Cancel</button>
            <button className="btn btn-danger" onClick={confirmDelete} disabled={deleting}>
              {deleting ? 'Deleting…' : 'Delete'}
            </button>
          </div>
        </Modal>
      )}
    </div>
  );
}

function ResetPasswordModal({ member, onClose, onDone }) {
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setSaving(true);
    try {
      await resetStaffPassword(member.staffId, password);
      onDone();
    } catch (err) {
      setError(err.errors?.password || err.message);
      setSaving(false);
    }
  };

  return (
    <Modal title={`Reset password for ${member.name}`} onClose={() => !saving && onClose()}>
      <form onSubmit={submit} noValidate>
        {error && <div className="alert alert-error" role="alert">{error}</div>}
        <label htmlFor="rp-password">New password</label>
        <input id="rp-password" type="password" value={password} autoComplete="new-password"
          onChange={(e) => setPassword(e.target.value)} aria-invalid={error ? 'true' : undefined} />
        <div className="muted hint">8 to 72 characters</div>
        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={onClose} disabled={saving}>Cancel</button>
          <button type="submit" className="btn btn-primary btn-inline" disabled={saving}>
            {saving ? 'Saving…' : 'Change password'}
          </button>
        </div>
      </form>
    </Modal>
  );
}
