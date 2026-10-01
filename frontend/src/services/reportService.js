import { api } from './api.js';

export const getReportCard = (studentId) => api.get(`/reports/report-card/${encodeURIComponent(studentId)}`);
/** One class when standard is given, otherwise every class: [{ standard, students: [...] }]. */
export const getClassResults = (standard) => api.get('/reports/class-results', { standard });
