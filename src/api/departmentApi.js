import axios from "axios";

const BASE_URL = "http://localhost:8080/api/departments"; // adjust your backend URL

export const getDepartments = async () => {
  try {
    const res = await axios.get(BASE_URL);
    return res.data; // should return [{deptId, deptName}]
  } catch (err) {
    console.error("Failed to fetch departments:", err);
    return [];
  }
};
