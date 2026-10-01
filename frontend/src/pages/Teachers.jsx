import { useCallback, useEffect, useState } from 'react';
import Modal from '../components/Modal.jsx';
import TeacherForm from '../components/TeacherForm.jsx';
import { usePermissions } from '../hooks/usePermissions.js';
import {
  createTeacher, deleteTeacher, listTeachers, listTeachersWithManySubjects, updateTeacher,
} from '../services/teacherService.js';

const PAGE_SIZE = 10;

export default function Teachers() {
  const { has } = usePermissions();
  const canAdd = has('TEACHER_ADD');
  const canEdit = has('TEACHER_UPDATE');
  const canDelete = has('TEACHER_DELETE');

  const [searchText, setSearchText] = useState('');
  const [subject, setSubject] = useState(''); // applied subject filter
  const [manyOnly, setManyOnly] = useState(false); // "more than 2 subjects" view (not paged)
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null); // { content, page, totalPages, totalElements }
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [formFor, setFormFor] = useState(null); // null | 'new' | teacher
  const [toDelete, setToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      if (manyOnly) {
        const list = await listTeachersWithManySubjects();
        setData({ content: list, page: 0, totalPages: 0, totalElements: list.length });
        return;
      }
      const result = await listTeachers({ subject, page, size: PAGE_SIZE });
      if (result.content.length === 0 && page > 0) {
        setPage(page - 1);
        return;
      }
      setData(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [subject, manyOnly, page]);

  useEffect(() => {
    load();
  }, [load]);

  const applySearch = (e) => {
    e.preventDefault();
    setManyOnly(false);
    setPage(0);
    setSubject(searchText.trim());
  };

  const toggleMany = () => {
    setSearchText('');
    setSubject('');
    setPage(0);
    setManyOnly((v) => !v);
  };

  const clearFilters = () => {
    setSearchText('');
    setSubject('');
    setManyOnly(false);
    setPage(0);
  };

  const save = async (payload) => {
    if (formFor === 'new') {
      await createTeacher(payload);
      setNotice(`Teacher ${payload.teacherId} added.`);
    } else {
      await updateTeacher(formFor.teacherId, payload);
      setNotice(`Teacher ${formFor.teacherId} updated.`);
    }
    setFormFor(null);
    await load();
  };

  const confirmDelete = async () => {
    setDeleting(true);
    try {
      await deleteTeacher(toDelete.teacherId);
      setNotice(`Teacher ${toDelete.teacherId} deleted.`);
      setToDelete(null);
      await load();
    } catch (err) {
      setError(err.message);
      setToDelete(null);
    } finally {
      setDeleting(false);
    }
  };

  const filtered = subject || manyOnly;
  const showActions = canEdit || canDelete;

  return (
    <div>
      <div className="page-header">
        <h1>Teachers</h1>
        {canAdd && (
          <button className="btn btn-primary btn-inline" onClick={() => { setNotice(''); setFormFor('new'); }}>
            + Add teacher
          </button>
        )}
      </div>

      <form className="toolbar" onSubmit={applySearch}>
        <input
          type="search" placeholder="Filter by subject (e.g. Maths)…" value={searchText}
          onChange={(e) => setSearchText(e.target.value)} aria-label="Filter by subject"
        />
        <button type="submit" className="btn btn-secondary">Search</button>
        <button type="button" className={`btn ${manyOnly ? 'btn-primary btn-inline' : 'btn-secondary'}`} onClick={toggleMany}>
          More than 2 subjects
        </button>
        {filtered && <button type="button" className="btn btn-secondary" onClick={clearFilters}>Clear</button>}
      </form>

      {notice && <div className="alert alert-success" role="status">{notice}</div>}
      {error && <div className="alert alert-error" role="alert">{error}</div>}

      <div className="card table-card">
        {loading && !data ? (
          <p className="muted pad">Loading teachers…</p>
        ) : data && data.content.length === 0 ? (
          <p className="muted pad">{filtered ? 'No teachers match your filter.' : 'No teachers yet.'}</p>
        ) : (
          data && (
            <div className="table-scroll">
              <table className="table">
                <thead>
                  <tr>
                    <th>ID</th><th>Name</th><th>Subjects</th><th>Qualification</th>
                    <th>Email</th><th>Contact</th>{showActions && <th></th>}
                  </tr>
                </thead>
                <tbody>
                  {data.content.map((t) => (
                    <tr key={t.teacherId}>
                      <td>{t.teacherId}</td>
                      <td>{t.name}</td>
                      <td className="wrap">{t.subjects.join(', ')}</td>
                      <td>{t.qualification}</td>
                      <td>{t.email}</td>
                      <td>{t.contactNumber}</td>
                      {showActions && (
                        <td className="row-actions">
                          {canEdit && <button className="btn btn-link" onClick={() => { setNotice(''); setFormFor(t); }}>Edit</button>}
                          {canDelete && <button className="btn btn-link danger" onClick={() => setToDelete(t)}>Delete</button>}
                        </td>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )
        )}
      </div>

      {data && !manyOnly && data.totalPages > 0 && (
        <div className="pager">
          <button className="btn btn-secondary" disabled={page === 0 || loading} onClick={() => setPage(page - 1)}>Previous</button>
          <span className="muted">Page {data.page + 1} of {data.totalPages} · {data.totalElements} teachers</span>
          <button className="btn btn-secondary" disabled={page + 1 >= data.totalPages || loading} onClick={() => setPage(page + 1)}>Next</button>
        </div>
      )}
      {data && manyOnly && data.totalElements > 0 && (
        <div className="pager"><span className="muted">{data.totalElements} teachers with more than 2 subjects</span></div>
      )}

      {formFor && (
        <Modal title={formFor === 'new' ? 'Add teacher' : `Edit ${formFor.teacherId}`} onClose={() => setFormFor(null)}>
          <TeacherForm teacher={formFor === 'new' ? null : formFor} onSubmit={save} onCancel={() => setFormFor(null)} />
        </Modal>
      )}

      {toDelete && (
        <Modal title="Delete teacher?" onClose={() => !deleting && setToDelete(null)}>
          <p>Delete <strong>{toDelete.name}</strong> ({toDelete.teacherId})? This cannot be undone.</p>
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
