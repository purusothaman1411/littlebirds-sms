import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import { usePermissions } from '../hooks/usePermissions.js';

/**
 * Wrap a page to require a login, and optionally a permission.
 *   <ProtectedRoute>                                        any logged-in user
 *   <ProtectedRoute permission="STAFF_MANAGE">              needs that permission
 *   <ProtectedRoute anyOf={['REPORT_CLASS_VIEW', 'REPORT_CARD_VIEW']}>
 */
export default function ProtectedRoute({ permission, anyOf, children }) {
  const { isAuthenticated, loading } = useAuth();
  const { has, hasAny } = usePermissions();
  const location = useLocation();

  if (loading) return <div className="page-message">Loading…</div>;

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  const allowed = permission ? has(permission) : anyOf ? hasAny(anyOf) : true;
  if (!allowed) return <Navigate to="/unauthorized" replace />;

  return children;
}
