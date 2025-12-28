import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Eye, EyeOff } from "lucide-react"; // Import icons

export default function AdminLogin({ setUser }) {
  const [usernameOrEmail, setUsernameOrEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false); // Toggle state
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ 
          usernameOrEmail: usernameOrEmail, 
          password: password 
        }),
      });

      const data = await response.json();

      if (response.ok && data.userId) {
        const adminSession = { 
          userId: data.userId, 
          role: data.role || "admin", 
          email: data.email,
          username: data.username 
        };
        
        localStorage.setItem("user_session", JSON.stringify(adminSession));
        setUser(adminSession);
        navigate("/admin/dashboard");
      } else {
        alert(data.message || "Invalid Admin Credentials!");
      }
    } catch (error) {
      console.error("Login error:", error);
      alert("Backend server unreachable.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <div className="auth-logo">NAST</div>
          <h2>Welcome Back</h2>
          <p>Please enter your admin credentials</p>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Admin Email or Username</label>
            <input
              type="text"
              placeholder="Enter your email or username"
              value={usernameOrEmail}
              onChange={(e) => setUsernameOrEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Password</label>
            <div className="password-wrapper" style={{ position: "relative" }}>
              <input
                type={showPassword ? "text" : "password"} // Dynamic type
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                style={{ width: "100%", paddingRight: "40px" }}
              />
              <button
                type="button"
                className="password-toggle-btn"
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
                  display: "flex",
                  alignItems: "center"
                }}
              >
                {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
              </button>
            </div>
          </div>

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? "Signing In..." : "Sign In"}
          </button>
        </form>

        <div className="auth-footer">
          <button onClick={() => navigate("/")} className="btn-text">
            ← Back to Portal
          </button>
          <button onClick={() => navigate("/admin/forgot-password")} className="btn-text">
            Forgot Password?
          </button>
        </div>
      </div>
    </div>
  );
}