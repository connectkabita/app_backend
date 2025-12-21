import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";

/* ================= LAYOUTS ================= */
import EmployeeLayout from "./components/EmployeeLayout";
import AdminLayout from "./components/AdminLayout"; 
import AccountantLayout from "./components/AccountantLayout"; 

/* ================= COMMON ================= */
import Landing from "./pages/Landing";

/* ================= LOGIN ================= */
import AdminLogin from "./pages/Login/AdminLogin";
import AccountantLogin from "./pages/Login/AccountantLogin";
import EmployeeLogin from "./pages/Login/EmployeeLogin";

/* ================= ADMIN PAGES ================= */
import AdminDashboard from "./pages/Admin/AdminDashboard";
import Employees from "./pages/Admin/Employees";
import Attendance from "./pages/Admin/Attendance";
import Leave from "./pages/Admin/Leave";
import AdminPayroll from "./pages/Admin/Payroll"; 
import Report from "./pages/Admin/Report";
import SystemConfig from "./pages/Admin/System-Config";

/* ================= ACCOUNTANT PAGES ================= */
import AccountantDashboard from "./pages/Accountant/AccountantDashboard"; 
import AccountantPayroll from "./pages/Accountant/Payroll"; 
import AccountantReport from "./pages/Accountant/Report";
import Tax from "./pages/Accountant/Tax"; 
import Salary from "./pages/Accountant/Salary"; 

/* ================= EMPLOYEE PAGES ================= */
import EmployeeDashboard from "./pages/Employee/EmployeeDashboard";
import AttendanceRecords from "./pages/Employee/AttendanceRecords";
import LeaveManagement from "./pages/Employee/LeaveManagement";
import SalaryAnalytics from "./pages/Employee/SalaryAnalytics";
import Settings from "./pages/Employee/Settings";

/* ================= IMPROVED AUTH GUARD ================= */
const ProtectedRoute = ({ allowedRole }) => {
  const savedUser = localStorage.getItem("user_session");
  const user = savedUser ? JSON.parse(savedUser) : null;

  if (!user) {
    return <Navigate to="/" replace />;
  }

  // Case-insensitive check and trim to avoid hidden space errors
  const userRole = user.role.toLowerCase().trim();
  const requiredRole = allowedRole.toLowerCase().trim();

  if (userRole !== requiredRole) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />; // We use Outlet here for cleaner nesting
};

import { Outlet } from "react-router-dom";

function App() {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem("user_session");
    return savedUser ? JSON.parse(savedUser) : null;
  });

  return (
    <Router>
      <Routes>
        <Route path="/" element={<Landing />} />

        {/* LOGIN ROUTES */}
        <Route path="/login/admin" element={<AdminLogin setUser={setUser} />} />
        <Route path="/login/accountant" element={<AccountantLogin setUser={setUser} />} />
        <Route path="/login/employee" element={<EmployeeLogin setUser={setUser} />} />

        {/* ACCOUNTANT PANEL - NESTED STRUCTURE */}
        <Route path="/accountant" element={<ProtectedRoute allowedRole="accountant" />}>
          <Route element={<AccountantLayout />}>
            <Route index element={<Navigate to="dashboard" replace />} />
            <Route path="dashboard" element={<AccountantDashboard />} />
            <Route path="payroll-processing" element={<AccountantPayroll />} />
            <Route path="salary-management" element={<Salary />} />
            <Route path="tax-compliance" element={<Tax />} />
            <Route path="financial-reports" element={<AccountantReport />} />
            {/* Fallbacks for older links */}
            <Route path="payroll" element={<AccountantPayroll />} />
            <Route path="salary" element={<Salary />} />
            <Route path="tax" element={<Tax />} />
            <Route path="report" element={<AccountantReport />} />
          </Route>
        </Route>

        {/* ADMIN PANEL */}
        <Route path="/admin" element={<ProtectedRoute allowedRole="admin" />}>
          <Route element={<AdminLayout />}>
            <Route index element={<Navigate to="dashboard" replace />} />
            <Route path="dashboard" element={<AdminDashboard />} />
            <Route path="employees" element={<Employees />} />
            <Route path="attendance" element={<Attendance />} />
            <Route path="leave" element={<Leave />} />
            <Route path="payroll" element={<AdminPayroll />} />
            <Route path="report" element={<Report />} />
            <Route path="system-config" element={<SystemConfig />} />
          </Route>
        </Route>

        {/* EMPLOYEE PANEL */}
        <Route path="/employee" element={<ProtectedRoute allowedRole="employee" />}>
          <Route element={<EmployeeLayout />}>
            <Route index element={<Navigate to="dashboard" replace />} />
            <Route path="dashboard" element={<EmployeeDashboard />} />
            <Route path="attendance" element={<AttendanceRecords />} />
            <Route path="leave" element={<LeaveManagement />} />
            <Route path="salary" element={<SalaryAnalytics />} />
            <Route path="settings" element={<Settings />} />
          </Route>
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default App;