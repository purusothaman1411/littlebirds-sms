import { api } from './api.js';

export const getStudentMarks = (id) => api.get(`/students/${encodeURIComponent(id)}/marks`);
/** marks = { "Maths": 80, ... }: only the subjects sent are added or updated. */
export const saveStudentMarks = (id, marks) => api.put(`/students/${encodeURIComponent(id)}/marks`, { marks });
export const getHighest = (subject) => api.get('/marks/highest', { subject });
export const getPassed = () => api.get('/marks/passed');
export const getAbove = (percent) => api.get('/marks/above', { percent });
export const getRanking = () => api.get('/marks/ranking');
