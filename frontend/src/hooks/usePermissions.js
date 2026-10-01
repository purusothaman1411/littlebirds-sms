import { useAuth } from '../context/AuthContext.jsx';

/**
 * Only decides what to SHOW. The permission list comes from the server with the login response
 * (so there is no second copy of the matrix here), and the backend still checks every request.
 */
export function usePermissions() {
  const { user } = useAuth();
  const granted = new Set(user?.permissions ?? []);

  return {
    has: (permission) => granted.has(permission),
    hasAny: (permissions) => permissions.some((p) => granted.has(p)),
  };
}
