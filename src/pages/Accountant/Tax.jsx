import React from 'react';
import './Tax.css';

const Tax = () => {
  const taxSlabs = [
    { id: 1, range: "Up to Rs. 500,000", rate: "1%", category: "Social Security Tax" },
    { id: 2, range: "Rs. 500,001 - Rs. 700,000", rate: "10%", category: "Income Tax" },
    { id: 3, range: "Rs. 700,001 - Rs. 1,000,000", rate: "20%", category: "Income Tax" },
    { id: 4, range: "Above Rs. 1,000,000", rate: "30%", category: "Income Tax" }
  ];

  return (
    <div className="prof-container">
      <div className="prof-header">
        <div>
          <h1>Tax & Compliance</h1>
          <p>Government Tax Slabs and SSF Regulations for FY 2025/26</p>
        </div>
        <button className="prof-action-btn">Update Slabs</button>
      </div>

      <div className="prof-card table-card">
        <div className="card-header">
          <h3>Active Tax Slabs</h3>
        </div>
        <table className="prof-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Annual Income Range</th>
              <th>Tax Rate</th>
              <th>Category</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {taxSlabs.map((slab) => (
              <tr key={slab.id}>
                <td>#0{slab.id}</td>
                <td className="bold-text">{slab.range}</td>
                <td><span className="rate-pill">{slab.rate}</span></td>
                <td>{slab.category}</td>
                <td><span className="status-indicator">Active</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Tax;