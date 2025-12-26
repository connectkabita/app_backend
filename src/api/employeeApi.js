import axios from "axios";

const API_URL = "http://localhost:8080/api/employees";

const getAuthHeaders = () => ({
  headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
});

export const getEmployees = (id) =>
  id ? axios.get(`${API_URL}/${id}`, getAuthHeaders()) : axios.get(API_URL, getAuthHeaders());

export const createEmployee = (employee) => axios.post(API_URL, employee, getAuthHeaders());

export const updateEmployee = (id, employee) => axios.put(`${API_URL}/${id}`, employee, getAuthHeaders());

export const deleteEmployee = (id) => axios.delete(`${API_URL}/${id}`, getAuthHeaders());

export const getActiveEmployeeStats = () => axios.get(`${API_URL}/stats/active-per-month`, getAuthHeaders());
