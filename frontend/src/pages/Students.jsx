import { useCallback, useEffect, useState } from 'react';
import Modal from '../components/Modal.jsx';
import StudentForm from '../components/StudentForm.jsx';
import { usePermissions } from '../hooks/usePermissions.js';
import { createStudent, deleteStudent, listStudents, updateStudent } from '../services/studentService.js';

const PAGE_SIZE = 10;

export default function Students() {
  const { has } = usePermissions();
  const canAdd = has('STUDENT_ADD');
  const canEdit = has('STUDENT_UPDATE');
  const canDelete = has('STUDENT_DELETE');

  const [searchText, setSearchText] = useState('');
  const [filters, setFilters] = useState({ name: '', standard: '' }); // applied filters
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null); // PageResponse
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [formFor, setFormFor] = useState(null); // null | 'new' | student
  const [toDelete, setToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const result = await listStudents({ ...filters, page, size: PAGE_SIZE });
      // if the last row of the last page was deleted, step back one page
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
  }, [filters, page]);

  useEffect(() => {
    load();
  }, [load]);

  const applySearch = (e) => {
    e.preventDefault();
    setPage(0);
    setFilters((f) => ({ ...f, name: searchText.trim() }));
  };

  const changeStandard = (e) => {
    setPage(0);
    setFilters((f) => ({ ...f, standard: e.target.value }));
  };

  const clearFilters = () => {
    setSearchText('');
    setPage(0);
    setFilters({ name: '', standard: '' });
  };

  const save = async (payload) => {
    if (formFor === 'new') {
      await createStudent(payload);
      setNotice(`Student ${payload.studentId} added.`);
    } else {
      await updateStudent(formFor.studentId, payload);
      setNotice(`Student ${formFor.studentId} updated.`);
    }
    setFormFor(null);
    await load();
  };

  const confirmDelete = async () => {
    setDeleting(true);
    try {
      await deleteStudent(toDelete.studentId);
      setNotice(`Student ${toDelete.studentId} deleted.`);
      setToDelete(null);
      await load();
    } catch (err) {
      setError(err.message);
      setToDelete(null);
    } finally {
      setDeleting(false);
    }
  };

  const filtered = filters.name || filters.standard;
  const showActions = canEdit || canDelete;

  return (
    <div>
      <div className="page-header">
        <h1>Students</h1>
        {canAdd && (
          <button className="btn btn-primary btn-inline" onClick={() => { setNotice(''); setFormFor('new'); }}>
            + Add student
          </button>
        )}
      </div>

      <form className="toolbar" onSubmit={applySearch}>
        <input
          type="search" placeholder="Search by name…" value={searchText}
          onChange={(e) => setSearchText(e.target.value)} aria-label="Search by name"
        />
        <select value={filters.standard} onChange={changeStandard} aria-label="Filter by standard">
          <option value="">All standards</option>
          {Array.from({ length: 12 }, (_, i) => i + 1).map((n) => <option key={n} value={n}>Standard {n}</option>)}
        </select>
        <button type="submit" className="btn btn-secondary">Search</button>
        {filtered && <button type="button" className="btn btn-secondary" onClick={clearFilters}>Clear</button>}
      </form>

      {notice && <div className="alert alert-success" role="status">{notice}</div>}
      {error && <div className="alert alert-error" role="alert">{error}</div>}

      <div className="card table-card">
        {loading && !data ? (
          <p className="muted pad">Loading students…</p>
        ) : data && data.content.length === 0 ? (
          <p className="muted pad">{filtered ? 'No students match your search.' : 'No students yet.'}</p>
        ) : (
          data && (
            <div className="table-scroll">
              <table className="table">
                <thead>
                  <tr>
                    <th>ID</th><th>Name</th><th>Std</th><th>Group</th><th>Gender</th>
                    <th>Age</th><th>Contact</th>{showActions && <th></th>}
                  </tr>
                </thead>
                <tbody>
                  {data.content.map((s) => (
                    <tr key={s.studentId}>
                      <td>{s.studentId}</td>
                      <td>{s.name}</td>
                      <td>{s.standard}</td>
                      <td>{s.group ?? '–'}</td>
                      <td>{s.gender}</td>
                      <td>{s.age}</td>
                      <td>{s.contactNumber}</td>
                      {showActions && (
                        <td className="row-actions">
                          {canEdit && <button className="btn btn-link" onClick={() => { setNotice(''); setFormFor(s); }}>Edit</button>}
                          {canDelete && <button className="btn btn-link danger" onClick={() => setToDelete(s)}>Delete</button>}
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

      {data && data.totalPages > 0 && (
        <div className="pager">
          <button className="btn btn-secondary" disabled={page === 0 || loading} onClick={() => setPage(page - 1)}>Previous</button>
          <span className="muted">Page {data.page + 1} of {data.totalPages} · {data.totalElements} students</span>
          <button className="btn btn-secondary" disabled={page + 1 >= data.totalPages || loading} onClick={() => setPage(page + 1)}>Next</button>
        </div>
      )}

      {formFor && (
        <Modal title={formFor === 'new' ? 'Add student' : `Edit ${formFor.studentId}`} onClose={() => setFormFor(null)}>
          <StudentForm student={formFor === 'new' ? null : formFor} onSubmit={save} onCancel={() => setFormFor(null)} />
        </Modal>
      )}

      {toDelete && (
        <Modal title="Delete student?" onClose={() => !deleting && setToDelete(null)}>
          <p>
            Delete <strong>{toDelete.name}</strong> ({toDelete.studentId})? Their marks and attendance will be
            deleted too. This cannot be undone.
          </p>
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
