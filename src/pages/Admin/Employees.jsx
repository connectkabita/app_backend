import { useEffect, useState } from "react";
import {
  getEmployees,
  createEmployee,
  updateEmployee,
  deleteEmployee,
  getActiveEmployeeStats,
} from "../../api/employeeApi";

import { getDepartments } from "../../api/departmentApi";
import { getDesignations } from "../../api/designationApi";

import ConfirmModal from "../../components/ConfirmModal";
import "./Employees.css";

export default function Employees() {
  const [employees, setEmployees] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [designations, setDesignations] = useState([]);

  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({});
  const [addingNew, setAddingNew] = useState(false);
  const [searchId, setSearchId] = useState("");
  const [activeStats, setActiveStats] = useState({});

  const [showConfirm, setShowConfirm] = useState(false);
  const [deleteId, setDeleteId] = useState(null);

  useEffect(() => {
    fetchEmployees();
    fetchDepartments();
    fetchDesignations();
    fetchActiveStats();
  }, []);

  const fetchEmployees = async () => {
    try {
      const res = await getEmployees();
      setEmployees(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchDepartments = async () => {
    const depts = await getDepartments();
    setDepartments(depts);
  };

  const fetchDesignations = async () => {
    const desigs = await getDesignations();
    setDesignations(desigs);
  };

  const fetchActiveStats = async () => {
    try {
      const res = await getActiveEmployeeStats();
      setActiveStats(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSearch = async () => {
    if (!searchId) return fetchEmployees();
    try {
      const res = await getEmployees(searchId);
      setEmployees([res.data]);
    } catch (err) {
      console.error(err);
      setEmployees([]);
    }
  };

  const startEdit = (emp) => {
    setEditingId(emp.empId);
    setFormData({
      ...emp,
      deptId: emp.department?.deptId,
      designationId: emp.position?.designationId,
    });
  };

  const cancelEdit = () => {
    setEditingId(null);
    setFormData({});
    setAddingNew(false);
  };

  const saveEdit = async (id) => {
    try {
      const payload = {
        ...formData,
        department: { deptId: formData.deptId },
        position: { designationId: formData.designationId },
      };

      if (addingNew) {
        await createEmployee(payload);
      } else {
        await updateEmployee(id, payload);
      }

      fetchEmployees();
      fetchActiveStats();
      cancelEdit();
    } catch (err) {
      console.error(err);
    }
  };

  // Modal-based delete
  const handleDeleteClick = (id) => {
    setDeleteId(id);
    setShowConfirm(true);
  };

  const confirmDelete = async () => {
    try {
      await deleteEmployee(deleteId);
      fetchEmployees();
      fetchActiveStats();
    } catch (err) {
      console.error(err);
    } finally {
      setShowConfirm(false);
      setDeleteId(null);
    }
  };

  const cancelDelete = () => {
    setShowConfirm(false);
    setDeleteId(null);
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === "checkbox" ? checked : value,
    });
  };

  return (
    <>
      <h1>Employees</h1>

      <div className="search-container">
        <input
          type="number"
          placeholder="Search by Employee ID"
          value={searchId}
          onChange={(e) => setSearchId(e.target.value)}
        />
        <button onClick={handleSearch}>Search</button>
      </div>

      <div className="active-stats">
        <h3>Active Employees Per Month</h3>
        <ul>
          {Object.keys(activeStats).map((month) => (
            <li key={month}>
              Month {month}: {activeStats[month]} Active
            </li>
          ))}
        </ul>
      </div>

      <button
        className="add-btn"
        onClick={() => {
          setAddingNew(true);
          setFormData({});
        }}
      >
        Add New Employee
      </button>

      <div className="table-table-container scrollable-table">
        <table className="employee-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Email</th>
              <th>Contact</th>
              <th>Marital Status</th>
              <th>Designation</th>
              <th>Education</th>
              <th>Employment Status</th>
              <th>Joining Date</th>
              <th>Address</th>
              <th>Department</th>
              <th>Active</th>
              <th>Created At</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {/* Add New Employee Row */}
            {addingNew && (
              <tr>
                <td>New</td>
                <td><input name="firstName" value={formData.firstName || ""} onChange={handleChange} /></td>
                <td><input name="lastName" value={formData.lastName || ""} onChange={handleChange} /></td>
                <td><input name="email" value={formData.email || ""} onChange={handleChange} /></td>
                <td><input name="contact" value={formData.contact || ""} onChange={handleChange} /></td>
                <td><input name="maritalStatus" value={formData.maritalStatus || ""} onChange={handleChange} /></td>

                {/* Designation Dropdown */}
                <td>
                  <select name="designationId" value={formData.designationId || ""} onChange={handleChange}>
                    <option value="">Select Designation</option>
                    {designations.map((d) => (
                      <option key={d.designationId} value={d.designationId}>{d.designationTitle}</option>
                    ))}
                  </select>
                </td>

                <td><input name="education" value={formData.education || ""} onChange={handleChange} /></td>
                <td><input name="employmentStatus" value={formData.employmentStatus || ""} onChange={handleChange} /></td>
                <td><input type="date" name="joiningDate" value={formData.joiningDate || ""} onChange={handleChange} /></td>
                <td><input name="address" value={formData.address || ""} onChange={handleChange} /></td>

                {/* Department Dropdown */}
                <td>
                  <select name="deptId" value={formData.deptId || ""} onChange={handleChange}>
                    <option value="">Select Department</option>
                    {departments.map((d) => (
                      <option key={d.deptId} value={d.deptId}>{d.deptName}</option>
                    ))}
                  </select>
                </td>

                <td><input type="checkbox" name="isActive" checked={formData.isActive || false} onChange={handleChange} /></td>
                <td>Auto</td>
                <td>
                  <button onClick={() => saveEdit(null)}>Save</button>
                  <button onClick={cancelEdit}>Cancel</button>
                </td>
              </tr>
            )}

            {/* Employee List */}
            {employees.map((emp) => (
              <tr key={emp.empId}>
                <td>{emp.empId}</td>
                <td>{editingId === emp.empId ? <input name="firstName" value={formData.firstName} onChange={handleChange} /> : emp.firstName}</td>
                <td>{editingId === emp.empId ? <input name="lastName" value={formData.lastName} onChange={handleChange} /> : emp.lastName}</td>
                <td>{editingId === emp.empId ? <input name="email" value={formData.email} onChange={handleChange} /> : emp.email}</td>
                <td>{editingId === emp.empId ? <input name="contact" value={formData.contact} onChange={handleChange} /> : emp.contact}</td>
                <td>{editingId === emp.empId ? <input name="maritalStatus" value={formData.maritalStatus} onChange={handleChange} /> : emp.maritalStatus}</td>

                {/* Designation Dropdown for Update */}
                <td>
                  {editingId === emp.empId ? (
                    <select name="designationId" value={formData.designationId || ""} onChange={handleChange}>
                      <option value="">Select Designation</option>
                      {designations.map((d) => (
                        <option key={d.designationId} value={d.designationId}>{d.designationTitle}</option>
                      ))}
                    </select>
                  ) : (
                    emp.position?.designationTitle
                  )}
                </td>

                <td>{editingId === emp.empId ? <input name="education" value={formData.education} onChange={handleChange} /> : emp.education}</td>
                <td>{editingId === emp.empId ? <input name="employmentStatus" value={formData.employmentStatus} onChange={handleChange} /> : emp.employmentStatus}</td>
                <td>{editingId === emp.empId ? <input type="date" name="joiningDate" value={formData.joiningDate} onChange={handleChange} /> : emp.joiningDate}</td>
                <td>{editingId === emp.empId ? <input name="address" value={formData.address} onChange={handleChange} /> : emp.address}</td>

                {/* Department Dropdown for Update */}
                <td>
                  {editingId === emp.empId ? (
                    <select name="deptId" value={formData.deptId || ""} onChange={handleChange}>
                      <option value="">Select Department</option>
                      {departments.map((d) => (
                        <option key={d.deptId} value={d.deptId}>{d.deptName}</option>
                      ))}
                    </select>
                  ) : (
                    emp.department?.deptName
                  )}
                </td>

                <td>{editingId === emp.empId ? <input type="checkbox" name="isActive" checked={formData.isActive} onChange={handleChange} /> : emp.isActive ? "Yes" : "No"}</td>
                <td>{emp.createdAt}</td>
                <td>
                  {editingId === emp.empId ? (
                    <>
                      <button onClick={() => saveEdit(emp.empId)}>Save</button>
                      <button onClick={cancelEdit}>Cancel</button>
                    </>
                  ) : (
                    <>
                      <button onClick={() => startEdit(emp)}>Update</button>
                      <button onClick={() => handleDeleteClick(emp.empId)}>Delete</button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Confirmation Modal */}
      <ConfirmModal
        show={showConfirm}
        message="Are you sure you want to delete this employee?"
        onConfirm={confirmDelete}
        onCancel={cancelDelete}
      />
    </>
  );
}
