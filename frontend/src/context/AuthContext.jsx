import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { hasToken, setUnauthorizedHandler } from '../services/api.js';
import * as authService from '../services/authService.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  // true while we ask the server whether a stored token is still valid
  const [loading, setLoading] = useState(hasToken());

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
  }, []);

  // If any request comes back 401, the login has expired: drop it so the app returns to the login page.
  useEffect(() => {
    setUnauthorizedHandler(logout);
  }, [logout]);

  // Restore the session after a refresh.
  useEffect(() => {
    if (!hasToken()) return;
    let cancelled = false;
    authService
      .fetchCurrentUser()
      .then((profile) => {
        if (!cancelled) setUser(profile);
      })
      .catch(() => {
        if (!cancelled) logout();
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [logout]);

  const login = useCallback(async (username, password) => {
    const profile = await authService.login(username, password);
    setUser(profile);
    return profile;
  }, []);

  const value = useMemo(
    () => ({ user, loading, isAuthenticated: user !== null, login, logout }),
    [user, loading, login, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used inside <AuthProvider>');
  return context;
}
