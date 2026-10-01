import ResultBadge from './ResultBadge.jsx';

/** Rows of StudentResultRow. With `ranked`, a Rank column is added (equal totals share a rank). */
export default function ResultsTable({ rows, ranked }) {
  if (rows.length === 0) return <p className="muted pad">No students found.</p>;

  let rank = 0;
  const ranks = rows.map((r, i) => {
    if (i === 0 || r.total !== rows[i - 1].total) rank = i + 1;
    return rank;
  });

  return (
    <div className="table-scroll">
      <table className="table">
        <thead>
          <tr>
            {ranked && <th>Rank</th>}
            <th>ID</th><th>Name</th><th>Std</th><th>Group</th><th>Total</th><th>%</th><th>Grade</th><th>Result</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={r.studentId}>
              {ranked && <td>{ranks[i]}</td>}
              <td>{r.studentId}</td>
              <td>{r.studentName}</td>
              <td>{r.standard}</td>
              <td>{r.group ?? '–'}</td>
              <td>{r.total}</td>
              <td>{r.percentage != null ? r.percentage : '–'}</td>
              <td>{r.grade ?? '–'}</td>
              <td><ResultBadge result={r.result} /></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
