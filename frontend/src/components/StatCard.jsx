import { Link } from 'react-router-dom';

/** value: a number, null when it could not be loaded, undefined while loading. */
export default function StatCard({ label, value, to }) {
  let shown = '…';
  if (value === null) shown = '–';
  else if (value !== undefined) shown = value;

  return (
    <Link to={to} className="stat-card">
      <div className="stat-value">{shown}</div>
      <div className="stat-label">{label}</div>
    </Link>
  );
}
