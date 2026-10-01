import { api } from './api.js';

/**
 * Totals for the dashboard cards. Only the totals the user may see are requested, and each one is
 * independent: if one request fails, the others still show (that card shows null).
 * Uses size=1 on the paged lists because only totalElements is needed.
 */
export async function fetchCounts(has) {
  const jobs = {};
  if (has('STUDENT_VIEW')) jobs.students = api.get('/students', { size: 1 }).then((p) => p.totalElements);
  if (has('TEACHER_VIEW')) jobs.teachers = api.get('/teachers', { size: 1 }).then((p) => p.totalElements);
  if (has('STAFF_MANAGE')) jobs.staff = api.get('/staff').then((list) => list.length);

  const keys = Object.keys(jobs);
  const results = await Promise.allSettled(keys.map((k) => jobs[k]));

  const counts = {};
  keys.forEach((key, i) => {
    counts[key] = results[i].status === 'fulfilled' ? results[i].value : null;
  });
  return counts;
}
