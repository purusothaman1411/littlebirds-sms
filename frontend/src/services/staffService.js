import { api } from './api.js';

export const ROLES = [
  { value: 'HEADMASTER', label: 'Headmaster' },
  { value: 'SUBJECT_STAFF', label: 'Subject Staff' },
  { value: 'WORKING_STAFF', label: 'Working Staff' },
  { value: 'MANAGEMENT_STAFF', label: 'Management Staff' },
];
export const roleLabel = (value) => ROLES.find((r) => r.value === value)?.label ?? value;
export const CLASS_RANGES = ['ALL', '1-10', '11-12'];

export const listStaff = () => api.get('/staff');
export const createStaff = (staff) => api.post('/staff', staff);
export const updateStaff = (id, staff) => api.put(`/staff/${encodeURIComponent(id)}`, staff);
export const resetStaffPassword = (id, password) => api.put(`/staff/${encodeURIComponent(id)}/password`, { password });
export const deleteStaff = (id) => api.delete(`/staff/${encodeURIComponent(id)}`);
