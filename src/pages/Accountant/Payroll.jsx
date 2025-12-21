import React, { useState } from 'react';
import './Payroll.css';

const Payroll = () => {
  const [employees, setEmployees] = useState([
    { id: "EMP501", name: "Anil Bhul", base: 60000, tax: 6000, net: 54000, status: "Pending" },
    { id: "EMP504", name: "Kabita Dhakal", base: 55000, tax: 5500, net: 49500, status: "Verified" },
    { id: "EMP509", name: "Bharat Gurdhami", base: 58000, tax: 5800, net: 52200, status: "Pending" }
  ]);

  const toggleStatus = (id) => {
    setEmployees(employees.map(emp => 
      emp.id === id ? { ...emp, status: emp.status === "Pending" ? "Verified" : "Pending" } : emp
    ));
  };

  return (
    <div className="payroll-container">
      <div className="payroll-header">
        <h2>Salary Disbursement Review</h2>
        <button className="batch-btn">Process All Verified</button>
      </div>

      <div className="payroll-table-card">
        <table className="payroll-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Base (Rs)</th>
              <th>Tax/SSF</th>
              <th>Net Pay</th>
              <th>Status</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {employees.map(emp => (
              <tr key={emp.id}>
                <td>{emp.id}</td>
                <td className="bold">{emp.name}</td>
                <td>{emp.base.toLocaleString()}</td>
                <td className="text-red">-{emp.tax.toLocaleString()}</td>
                <td className="text-green bold">{emp.net.toLocaleString()}</td>
                <td>
                  <span className={`badge ${emp.status.toLowerCase()}`}>{emp.status}</span>
                </td>
                <td>
                  <button className="verify-btn" onClick={() => toggleStatus(emp.id)}>
                    {emp.status === "Pending" ? "Verify" : "Undo"}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Payroll;