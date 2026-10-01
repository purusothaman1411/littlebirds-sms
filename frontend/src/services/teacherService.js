import { api } from './api.js';

export const listTeachers = ({ subject, page = 0, size = 10 }) => api.get('/teachers', { subject, page, size });
export const listTeachersWithManySubjects = () => api.get('/teachers/more-than-two-subjects');
export const createTeacher = (teacher) => api.post('/teachers', teacher);
export const updateTeacher = (id, teacher) => api.put(`/teachers/${encodeURIComponent(id)}`, teacher);
export const deleteTeacher = (id) => api.delete(`/teachers/${encodeURIComponent(id)}`);
