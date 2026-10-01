import { api } from './api.js';
import { GROUPS } from './studentService.js';

let cache = null;

/**
 * Every subject name in the curriculum. The backend only lists subjects for one class at a time,
 * so this asks for standard 1 and for standard 11 in each group, and merges the answers.
 */
export async function listAllSubjects() {
  if (cache) return cache;
  const calls = [
    api.get('/subjects', { standard: 1 }),
    ...GROUPS.map((group) => api.get('/subjects', { standard: 11, group })),
  ];
  const lists = await Promise.all(calls);
  const names = [...new Set(lists.flat())].sort((a, b) => a.localeCompare(b));
  cache = names;
  return names;
}
