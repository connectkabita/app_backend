import React from 'react';
import './Report.css';

const Report = () => {
  // Modules from project proposal: Salary, Tax, Attendance, and History 
  const reportCategories = [
    { title: "Salary Summaries", desc: "Monthly expenditure and net pay distribution", icon: "💰", type: "Salary" },
    { title: "Tax & SSF Reports", desc: "Government compliance and deduction records", icon: "🏛️", type: "Tax" },
    { title: "Attendance Logs", desc: "Verification of presence for payroll accuracy", icon: "🕒", type: "Attendance" },
    { title: "Employee History", desc: "Individual payroll and increment archives", icon: "👤", type: "History" }
  ];

  const recentReports = [
    { name: "Nov_2025_Payroll_Summary.pdf", date: "Nov 20, 2025", type: "Salary" },
    { name: "Q4_Tax_Compliance.xlsx", date: "Nov 18, 2025", type: "Tax" },
    { name: "Annual_Bonus_Report.pdf", date: "Nov 15, 2025", type: "Bonus" }
  ];

  const handleGenerate = (title) => {
    alert(`Generating ${title}... Please wait.`);
    // Future logic: Call API to generate PDF/Excel [cite: 63, 64]
  };

  const handleDownload = (fileName) => {
    alert(`Downloading ${fileName}`);
    // Future logic: Trigger file download [cite: 36]
  };

  return (
    <div className="report-container">
      <div className="report-header">
        <h1>Financial Reports</h1>
        <p>Analyze and export organizational payroll data [cite: 38]</p>
      </div>

      <div className="report-types-grid">
        {reportCategories.map((report, index) => (
          <div key={index} className="report-type-card">
            <div className="report-icon-box">{report.icon}</div>
            <div className="report-details">
              <h4>{report.title}</h4>
              <p>{report.desc}</p>
              <button 
                className="generate-btn" 
                onClick={() => handleGenerate(report.title)}
              >
                Generate Report
              </button>
            </div>
          </div>
        ))}
      </div>

      <h3 className="section-subtitle">Recently Generated</h3>

      <div className="recent-reports-wrapper">
        <table className="report-table">
          <thead>
            <tr>
              <th>File Name</th>
              <th>Category</th>
              <th>Date Generated</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {recentReports.map((file, index) => (
              <tr key={index}>
                <td className="file-name-cell">📄 {file.name}</td>
                <td>
                  <span className={`type-badge ${file.type.toLowerCase()}`}>
                    {file.type}
                  </span>
                </td>
                <td>{file.date}</td>
                <td>
                  <button 
                    className="download-link" 
                    onClick={() => handleDownload(file.name)}
                  >
                    Download
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

export default Report;