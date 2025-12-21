import React from 'react';
import './AccountantDashboard.css';

const AccountantDashboard = () => {
  return (
    <div className="pro-dash-content">
      <header className="pro-dash-header">
        <div className="header-text">
          <h1>Accountant <span className="highlight">Command Center</span></h1>
          <p>Real-time payroll status for NAST System • Fiscal Year 2025/26</p>
        </div>
        <div className="header-date">
          <span className="calendar-icon">📅</span> Dec 21, 2025
        </div>
      </header>

      <section className="kpi-stack-section">
        <h3 className="sub-section-title">Critical Metrics</h3>
        <div className="vertical-kpi-stack">
          {/* Monthly Payroll Row */}
          <div className="kpi-linear-card blue-glow">
            <div className="kpi-icon-container">💰</div>
            <div className="kpi-main-info">
              <span className="kpi-tag">Monthly Payroll</span>
              <h2 className="kpi-amount">Rs. 4.2M</h2>
            </div>
            <div className="kpi-meta">
              <span className="meta-label">Status</span>
              <span className="status-pill status-active">Processing</span>
            </div>
          </div>

          {/* Tax Compliance Row */}
          <div className="kpi-linear-card indigo-glow">
            <div className="kpi-icon-container">🏛️</div>
            <div className="kpi-main-info">
              <span className="kpi-tag">Tax & SSF Compliance</span>
              <h2 className="kpi-amount">100% Verified</h2>
            </div>
            <div className="kpi-meta">
              <span className="meta-label">Audit</span>
              <span className="status-pill status-secure">Government Compliant</span>
            </div>
          </div>

          {/* Pending Verifications Row */}
          <div className="kpi-linear-card amber-glow">
            <div className="kpi-icon-container">⏳</div>
            <div className="kpi-main-info">
              <span className="kpi-tag">Pending Verifications</span>
              <h2 className="kpi-amount">12 Records</h2>
            </div>
            <div className="kpi-meta">
              <span className="meta-label">Action</span>
              <span className="status-pill status-warn">Review Required</span>
            </div>
          </div>
        </div>
      </section>

      <section className="ops-grid-section">
        <h3 className="sub-section-title">Management Portals</h3>
        <div className="ops-portal-grid">
          <button className="portal-tile">
            <span className="tile-emoji">💸</span>
            <div className="tile-text">
              <h4>Calculate Salary</h4>
              <p>Process net disbursements</p>
            </div>
          </button>
          <button className="portal-tile">
            <span className="tile-emoji">⚖️</span>
            <div className="tile-text">
              <h4>Tax Management</h4>
              <p>Adjust slabs & SSF rates</p>
            </div>
          </button>
          <button className="portal-tile">
            <span className="tile-emoji">📂</span>
            <div className="tile-text">
              <h4>Payroll Ledger</h4>
              <p>Access historical archives</p>
            </div>
          </button>
          <button className="portal-tile active-portal">
            <span className="tile-emoji">📊</span>
            <div className="tile-text">
              <h4>Financial Reports</h4>
              <p>Generate CSV/PDF analytics</p>
            </div>
          </button>
        </div>
      </section>
    </div>
  );
};

export default AccountantDashboard;