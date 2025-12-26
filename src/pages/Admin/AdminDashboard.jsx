import React, { useState } from 'react';
import './AdminDashboard.css';

const AdminDashboard = () => {
  const [searchTerm, setSearchTerm] = useState('');

  const adminStats = [
    { title: "TOTAL WORKFORCE", value: "154", icon: "👥", color: "#4e73df" },
    { title: "DAILY ATTENDANCE", value: "92%", icon: "📅", color: "#1cc88a" },
    { title: "LEAVE REQUESTS", value: "08", icon: "📝", color: "#f6c23e" }
  ];

  const adminActions = [
    { title: "Employee Mgmt", desc: "Add or update staff records", icon: "👤" },
    { title: "Leave Approvals", desc: "Review pending requests", icon: "✅" },
    { title: "System Config", desc: "Manage system settings", icon: "⚙️" },
    { title: "Payroll Review", desc: "Verify monthly calculations", icon: "💰" }
  ];

  return (
    <div className="dashboard-container">
      {/* Scrollable Content */}
      <div className="dashboard-content">
        <div className="dashboard-header">
          <h1>Dashboard Overview</h1>
          <p>Real-time summary of the NAST Payroll Management System</p>
          <input
            type="text"
            placeholder="Search..."
            className="dashboard-search-bar"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        {/* Admin Stats */}
        <div className="top-stats-grid">
          {adminStats.map((stat, index) => (
            <div key={index} className="horizontal-stat-card">
              <div className="stat-icon-container">
                <span className="icon-main">{stat.icon}</span>
              </div>
              <div className="stat-text-content">
                <span className="stat-label-top">{stat.title}</span>
                <h2 className="stat-value-large">{stat.value}</h2>
              </div>
            </div>
          ))}
        </div>

        <h3 className="section-divider-title">Quick Actions</h3>

        <div className="actions-flex-grid">
          {adminActions.map((action, index) => (
            <div key={index} className="quick-action-box">
              <div className="action-icon-small">
                <span>{action.icon}</span>
              </div>
              <div className="action-info-text">
                <h4>{action.title}</h4>
                <p>{action.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </div>

  
    </div>
  );
};

export default AdminDashboard;
