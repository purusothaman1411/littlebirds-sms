import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import StatCard from '../components/StatCard.jsx';
import { NAV_ITEMS } from '../components/navItems.js';
import { useAuth } from '../context/AuthContext.jsx';
import { usePermissions } from '../hooks/usePermissions.js';
import { fetchCounts } from '../services/dashboardService.js';

const ROLE_LABELS = {
  HEADMASTER: 'Headmaster',
  SUBJECT_STAFF: 'Subject Staff',
  WORKING_STAFF: 'Working Staff',
  MANAGEMENT_STAFF: 'Management Staff',
};

export default function Dashboard() {
  const { user } = useAuth();
  const { has, hasAny } = usePermissions();
  const [counts, setCounts] = useState({});
  const [loaded, setLoaded] = useState(false);

  useEffect(() => {
    let cancelled = false;
    fetchCounts(has).then((result) => {
      if (!cancelled) {
        setCounts(result);
        setLoaded(true);
      }
    });
    return () => {
      cancelled = true;
    };
    // permissions never change during a session (the profile is fixed at login)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user.staffId]);

  // while loading a card's value is undefined; after loading, a missing value means "failed" (null)
  const valueFor = (key) => (loaded ? counts[key] ?? null : undefined);

  const quickLinks = NAV_ITEMS.filter((item) => {
    if (item.to === '/') return false;
    if (item.permission) return has(item.permission);
    if (item.anyOf) return hasAny(item.anyOf);
    return true;
  });

  return (
    <div>
      <h1>Welcome, {user.name}</h1>
      <p className="muted">
        Logged in as {ROLE_LABELS[user.role] ?? user.role} · Little Birds School
      </p>

      <div className="stat-grid">
        {has('STUDENT_VIEW') && <StatCard label="Students" value={valueFor('students')} to="/students" />}
        {has('TEACHER_VIEW') && <StatCard label="Teachers" value={valueFor('teachers')} to="/teachers" />}
        {has('STAFF_MANAGE') && <StatCard label="Staff accounts" value={valueFor('staff')} to="/staff" />}
      </div>

      <h2>Quick links</h2>
      <div className="link-grid">
        {quickLinks.map((item) => (
          <Link key={item.to} to={item.to} className="card link-card">
            <strong>{item.label}</strong>
            <span className="muted">{item.description}</span>
          </Link>
        ))}
      </div>
    </div>
  );
}
