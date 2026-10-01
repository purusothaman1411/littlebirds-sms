// The only place that calls fetch(). Adds the token, parses the backend's error shape
// ({ status, message, timestamp, errors }) and reports expired logins.

const BASE_URL = '/api';
const STORAGE_KEY = 'sms.session';

/** Error thrown for every failed request. `errors` is the field -> message map for validation failures. */
export class ApiError extends Error {
  constructor(status, message, errors) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.errors = errors || {};
  }
}

// ---- session storage (sessionStorage: cleared when the browser tab is closed) ----

export function loadSession() {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    const session = JSON.parse(raw);
    if (!session.token || (session.expiresAt && new Date(session.expiresAt) <= new Date())) {
      sessionStorage.removeItem(STORAGE_KEY);
      return null;
    }
    return session;
  } catch {
    return null;
  }
}

export function saveSession(token, expiresAt) {
  try {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify({ token, expiresAt }));
  } catch {
    /* storage blocked: the user will simply have to log in again after a refresh */
  }
}

export function clearSession() {
  try {
    sessionStorage.removeItem(STORAGE_KEY);
  } catch {
    /* ignore */
  }
}

// The token is also kept in memory so it still works if storage is blocked.
let memoryToken = loadSession()?.token ?? null;

export function setToken(token, expiresAt) {
  memoryToken = token;
  saveSession(token, expiresAt);
}

export function removeToken() {
  memoryToken = null;
  clearSession();
}

export function hasToken() {
  return memoryToken !== null;
}

// AuthContext registers a function here; it is called when the server says the login is no longer valid.
let onUnauthorized = () => {};
export function setUnauthorizedHandler(handler) {
  onUnauthorized = handler;
}

// ---- requests ----

async function request(method, path, { body, params, auth = true } = {}) {
  let url = BASE_URL + path;
  if (params) {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') query.append(key, value);
    });
    const text = query.toString();
    if (text) url += '?' + text;
  }

  const headers = { Accept: 'application/json' };
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (auth && memoryToken) headers.Authorization = `Bearer ${memoryToken}`;

  let response;
  try {
    response = await fetch(url, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new ApiError(0, 'Cannot reach the server. Check that the backend is running.');
  }

  let data = null;
  if (response.status !== 204) {
    const text = await response.text();
    if (text) {
      try {
        data = JSON.parse(text);
      } catch {
        data = null;
      }
    }
  }

  if (!response.ok) {
    // 401 on a protected call = expired or revoked login. (A failed login attempt is auth:false, so it is excluded.)
    if (response.status === 401 && auth) onUnauthorized();
    throw new ApiError(
      response.status,
      data?.message || `Request failed (${response.status})`,
      data?.errors
    );
  }
  return data;
}

export const api = {
  get: (path, params) => request('GET', path, { params }),
  post: (path, body, options) => request('POST', path, { body, ...options }),
  put: (path, body) => request('PUT', path, { body }),
  delete: (path) => request('DELETE', path),
};
