import { api } from './api.js';

// The groups for standards 11-12 (same list as the backend's CurriculumService).
export const GROUPS = ['Bio-Maths', 'Computer Science', 'Commerce', 'Humanities'];
export const GENDERS = ['Male', 'Female', 'Other'];

export const listStudents = ({ name, standard, page = 0, size = 10 }) =>
  api.get('/students', { name, standard, page, size });

export const createStudent = (student) => api.post('/students', student);
export const updateStudent = (id, student) => api.put(`/students/${encodeURIComponent(id)}`, student);
export const deleteStudent = (id) => api.delete(`/students/${encodeURIComponent(id)}`);

export const getStudent = (id) => api.get(`/students/${encodeURIComponent(id)}`);
