import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Report.css';

const Report = () => {
  const [recentReports, setRecentReports] = useState([]);
  const [isGenerating, setIsGenerating] = useState(false);

  const reportCategories = [
    { title: "Salary Summaries", desc: "Monthly expenditure and net pay distribution", icon: "💰" },
    { title: "Tax & SSF Reports", desc: "Government compliance and deduction records", icon: "🏛️" },
    { title: "Attendance Logs", desc: "Verification of presence for payroll accuracy", icon: "🕒" },
    { title: "Employee History", desc: "Individual payroll and increment archives", icon: "👤" }
  ];

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/reports/history');
      setRecentReports(res.data);
    } catch (err) {
      console.error("Failed to load reports history:", err);
    }
  };

  const handleGenerate = async (category) => {
    setIsGenerating(true);
    try {
      // Pass the full category name so backend can branch logic
      await axios.post(`http://localhost:8080/api/reports/generate?category=${category}`);
      fetchHistory(); 
    } catch (err) {
      alert("Error generating report. Please check if the backend is running.");
    } finally {
      setIsGenerating(false);
    }
  };

  const handleDownload = async (reportId, fileName) => {
    try {
      const response = await axios.get(`http://localhost:8080/api/reports/download/${reportId}`, {
        responseType: 'blob',
      });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName); 
      document.body.appendChild(link);
      link.click();
      
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Download failed:", error);
      alert("File download failed.");
    }
  };

  const formatDateTime = (dateString) => {
    if (!dateString) return "N/A";
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' | ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className="report-container">
      <div className="report-header">
        <h1>Financial Reports</h1>
        <p>Analyze and export organizational payroll data</p>
      </div>

      <div className="report-types-grid">
        {reportCategories.map((cat, i) => (
          <div key={i} className="report-type-card">
            <div className="report-icon-box">{cat.icon}</div>
            <div className="report-details">
              <h4>{cat.title}</h4>
              <p>{cat.desc}</p>
              <button 
                className="generate-btn" 
                onClick={() => handleGenerate(cat.title)} 
                disabled={isGenerating}
              >
                {isGenerating ? "Processing..." : "Generate Report"}
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
              <th>FILE NAME & SIZE</th>
              <th>CATEGORY</th>
              <th>GENERATED AT</th>
              <th>ACTION</th>
            </tr>
          </thead>
          <tbody>
            {recentReports.length > 0 ? (
              recentReports.map((file, index) => {
                // Normalize category name for CSS (e.g., "Tax & SSF Reports" -> "taxssfreports")
                const categoryClass = file.category.toLowerCase().replace(/[^a-z]/g, '');
                
                return (
                  <tr key={index}>
                    <td className="file-name-cell">
                      📄 {file.fileName} 
                      <span style={{color: '#94a3b8', fontSize: '0.7rem', marginLeft: '10px'}}>
                        {file.fileSize || '15 KB'}
                      </span>
                    </td>
                    <td>
                      <span className={`type-badge ${categoryClass}`}>
                        {file.category}
                      </span>
                    </td>
                    <td className="time-stamp">{formatDateTime(file.dateGenerated)}</td>
                    <td>
                      <button 
                        className="download-btn-ui" 
                        onClick={() => handleDownload(file.reportId, file.fileName)}
                      >
                        Download
                      </button>
                    </td>
                  </tr>
                );
              })
            ) : (
              <tr>
                <td colSpan="4" style={{textAlign: 'center', padding: '40px', color: '#94a3b8'}}>
                   No reports found in history.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Report;