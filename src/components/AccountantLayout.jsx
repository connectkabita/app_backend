import React from 'react';
import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import './AccountantLayout.css';

const AccountantLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const handleSignOut = () => {
    localStorage.removeItem("user_session");
    navigate("/");
  };

  return (
    <div className="accountant-container">
      {/* SIDEBAR SECTION */}
      <aside className="sidebar">
        <div className="sidebar-logo">
          <h2>NAST</h2>
        </div>
        <nav className="sidebar-menu">
          {/* CRITICAL: No leading slashes in 'to' paths */}
          <Link to="dashboard" className={location.pathname.includes('dashboard') ? 'active' : ''}>
            🏠 Dashboard
          </Link>
          <Link to="salary-management" className={location.pathname.includes('salary') ? 'active' : ''}>
            💸 Salary Management
          </Link>
          <Link to="payroll-processing" className={location.pathname.includes('payroll') ? 'active' : ''}>
            💰 Payroll Processing
          </Link>
          <Link to="tax-compliance" className={location.pathname.includes('tax') ? 'active' : ''}>
            📄 Tax & Compliance
          </Link>
          <Link to="financial-reports" className={location.pathname.includes('report') ? 'active' : ''}>
            📊 Financial Reports
          </Link>
        </nav>
        <button className="signout-btn" onClick={handleSignOut}>Sign Out</button>
      </aside>

      {/* MAIN CONTENT SECTION */}
      <main className="main-content">
        <header className="top-header">
          <div className="search-bar">
            <input type="text" placeholder="Search accounts..." />
          </div>
          <div className="user-info">
            <span>Accountant Finance Dept</span>
          </div>
        </header>

        <section className="page-body">
          {/* THE OUTLET RENDERS THE CHILD COMPONENTS (Tax, Salary, etc.) */}
          <Outlet />
        </section>
      </main>
    </div>
  );
};

export default AccountantLayout;