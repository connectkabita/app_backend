import React from 'react';
import './Salary.css';

const Salary = () => {
  const depts = [
    { name: "Computer Engineering", base: 2500000, tax: 250000, net: 2250000 },
    { name: "Administration", base: 1200000, tax: 120000, net: 1080000 },
    { name: "Exam Branch", base: 850000, tax: 85000, net: 765000 }
  ];

  return (
    <div className="prof-container">
      <div className="prof-header">
        <h1>Salary Summaries</h1>
        <p>Monthly payroll analytics and departmental expenditure</p>
      </div>

      <div className="metrics-grid">
        <div className="metric-card">
          <span>Total Gross Pay</span>
          <h2>Rs. 4.55M</h2>
        </div>
        <div className="metric-card red-border">
          <span>Total Deductions</span>
          <h2>Rs. 0.45M</h2>
        </div>
        <div className="metric-card green-border">
          <span>Total Net Disbursement</span>
          <h2>Rs. 4.10M</h2>
        </div>
      </div>

      <div className="prof-card">
        <div className="card-header">
          <h3>Departmental Breakdown</h3>
        </div>
        <div className="dept-list">
          {depts.map((d, i) => (
            <div key={i} className="dept-row">
              <div className="dept-info">
                <h4>{d.name}</h4>
                <p>Net Distribution: <strong>Rs. {d.net.toLocaleString()}</strong></p>
              </div>
              <div className="dept-progress-container">
                <div className="progress-label">Tax: Rs. {d.tax.toLocaleString()}</div>
                <div className="progress-bar"><div className="fill" style={{width: '85%'}}></div></div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Salary;