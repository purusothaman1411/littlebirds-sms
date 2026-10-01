import { Link } from 'react-router-dom';

export default function Unauthorized() {
  return (
    <div>
      <h1>Not allowed</h1>
      <p className="muted">Your role does not have access to that page.</p>
      <Link to="/">Back to the dashboard</Link>
    </div>
  );
}
