import { useState } from "react";
import { useNavigate } from "react-router-dom"; 
import api from "../../api/axios"; 
import { Eye, EyeOff } from "lucide-react"; // Import Icons
import "./login.css"; 

export default function EmployeeLogin({ setUser }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate(); 

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const trimmedEmail = email.trim();
      const trimmedPassword = password.trim();

      if (!trimmedEmail || !trimmedPassword) {
        alert("Please enter email/username and password");
        return;
      }

      const loginPayload = {
        usernameOrEmail: trimmedEmail,
        password: trimmedPassword,
        role: "Employee"
      };

      const response = await api.post("/auth/login", loginPayload, {
        headers: { 'Content-Type': 'application/json' }
      });

      localStorage.setItem("user_session", JSON.stringify(response.data));
      setUser(response.data);
      navigate("/employee/dashboard"); 

    } catch (err) {
      console.error("Employee login error:", err);
      const serverMessage = err.response?.data?.message || "Connection to server failed";
      alert(serverMessage);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <div className="auth-logo">NAST</div>
          <h2>Employee Portal</h2>
          <p>Access your payroll and attendance</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label>Email or Username</label>
            <input
              type="text"
              placeholder="Enter username or email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group" style={{ position: "relative" }}>
            <label>Password</label>
            <div style={{ position: "relative" }}>
              <input
                type={showPassword ? "text" : "password"}
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                style={{ width: "100%", paddingRight: "40px" }}
              />
              <button
                type="button" // Important: prevents form submission
                onClick={() => setShowPassword(!showPassword)}
                style={{
                  position: "absolute",
                  right: "10px",
                  top: "50%",
                  transform: "translateY(-50%)",
                  background: "none",
                  border: "none",
                  cursor: "pointer",
                  color: "#666",
                  display: "flex"
                }}
              >
                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
          </div>

          <button type="submit" className="btn-primary" style={{ backgroundColor: '#059669' }}>
            Sign In
          </button>
        </form>

        <div className="auth-footer">
          <button onClick={() => navigate("/")} className="btn-text">
            ← Back to Landing Page
          </button>
          <button onClick={() => navigate("/employee/forgot-password")} className="btn-text">
            Forgot Password?
          </button>
        </div>
      </div>
    </div>
  );
}