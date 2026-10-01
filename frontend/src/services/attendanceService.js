import { api } from './api.js';
import { listStudents } from './studentService.js';

/** Today as yyyy-MM-dd in the browser's local time (what <input type="date"> uses). */
export const todayISO = () => new Date().toLocaleDateString('en-CA');

/** Every student of a standard (the list API is paged, so read all pages). */
export async function listClassStudents(standard) {
  const all = [];
  let page = 0;
  let totalPages = 1;
  while (page < totalPages) {
    const result = await listStudents({ standard, page, size: 100 });
    all.push(...result.content);
    totalPages = result.totalPages;
    page += 1;
  }
  return all;
}

export const getAttendanceByDate = (date, standard) => api.get('/attendance', { date, standard });
export const markAttendance = (date, standard, entries) => api.post('/attendance', { date, standard, entries });
export const getStudentAttendance = (id) => api.get(`/students/${encodeURIComponent(id)}/attendance`);
