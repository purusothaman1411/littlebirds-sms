import { api, setToken, removeToken } from './api.js';

/** Returns the user profile { staffId, name, username, role, permissions } and keeps the token. */
export async function login(username, password) {
  const result = await api.post('/auth/login', { username, password }, { auth: false });
  setToken(result.token, result.expiresAt);
  return result.user;
}

/** Asks the server who the stored token belongs to (used to restore a session after a page refresh). */
export function fetchCurrentUser() {
  return api.get('/auth/me');
}

/** The server keeps no session, so logging out just forgets the token. */
export function logout() {
  removeToken();
}
