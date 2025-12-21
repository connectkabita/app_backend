import React from "react";

import { Outlet, NavLink, useNavigate } from "react-router-dom";

import "./AdminLayout.css";



const AdminLayout = () => {

  const navigate = useNavigate();



  const handleLogout = () => {

    localStorage.removeItem("user_session");

    navigate("/");

  };



  const menuItems = [

    { path: "dashboard", label: "Dashboard", icon: "🏠" },

    { path: "employees", label: "Employees", icon: "👥" },

    { path: "attendance", label: "Attendance", icon: "📅" },

    { path: "leave", label: "Leave Requests", icon: "📝" },

    { path: "payroll", label: "Payroll", icon: "💰" },

    { path: "system-config", label: "System Config", icon: "⚙️" },

    { path: "report", label: "Reports", icon: "📊" }

  ];



  return (

    <div className="admin-container">

      <aside className="admin-sidebar">

        <div className="sidebar-header">

          <div className="logo-box">NAST</div>

        </div>



        <nav className="sidebar-nav">

          {menuItems.map((item) => (

            <NavLink

              key={item.path}

              to={item.path}

              className={({ isActive }) => isActive ? "nav-item active" : "nav-item"}

            >

              <span className="nav-icon">{item.icon}</span>

              <span className="nav-label">{item.label}</span>

            </NavLink>

          ))}

        </nav>



        <button className="signout-btn" onClick={handleLogout}>Sign Out</button>

      </aside>



      <main className="admin-main">

        <header className="admin-top-bar">

          <div className="search-wrapper">

            <input type="text" placeholder="Search system records..." />

          </div>

          <div className="admin-profile">

            <div className="profile-info">

              <span className="name">Administrator</span>

              <span className="role">Super Admin</span>

            </div>

            <div className="avatar">AD</div>

          </div>

        </header>



        <div className="admin-content">

          <Outlet />

        </div>

      </main>

    </div>

  );

};



export default AdminLayout;