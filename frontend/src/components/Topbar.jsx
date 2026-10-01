import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

const ROLE_LABELS = {
  HEADMASTER: 'Headmaster',
  SUBJECT_STAFF: 'Subject Staff',
  WORKING_STAFF: 'Working Staff',
  MANAGEMENT_STAFF: 'Management Staff',
};

export default function Topbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const [darkMode, setDarkMode] = useState(() => {
    return localStorage.getItem('theme') !== 'light';
  });

  useEffect(() => {
    if (darkMode) {
      document.body.classList.remove('light-theme');
      localStorage.setItem('theme', 'dark');
    } else {
      document.body.classList.add('light-theme');
      localStorage.setItem('theme', 'light');
    }
  }, [darkMode]);

  const toggleTheme = () => {
    setDarkMode((prev) => !prev);
  };

  function handleLogout() {
    logout();
    navigate('/login', { replace: true });
  }

  return (
    <header className="topbar">

      <div className="topbar-title">
        School Management System
      </div>

      <div className="topbar-user">

        <span>
          {user.name}{' '}
          <small>
            ({ROLE_LABELS[user.role] ?? user.role})
          </small>
        </span>

        <button
          type="button"
          className="theme-toggle"
          onClick={toggleTheme}
        >
          <span className="theme-icon">
            {darkMode ? '☀️' : '🌙'}
          </span>

          <span>
            {darkMode ? 'Light' : 'Dark'}
          </span>
        </button>

        <button
          type="button"
          className="btn btn-secondary"
          onClick={handleLogout}
        >
          Log out
        </button>

      </div>

    </header>
  );
}