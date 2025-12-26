import axios from "axios";

const BASE_URL = "http://localhost:8080/api/designations"; // adjust your backend URL

export const getDesignations = async () => {
  try {
    const res = await axios.get(BASE_URL);
    return res.data; // should return [{designationId, designationTitle}]
  } catch (err) {
    console.error("Failed to fetch designations:", err);
    return [];
  }
};
