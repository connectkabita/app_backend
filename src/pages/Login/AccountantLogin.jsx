import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios"; 
import { Eye, EyeOff } from "lucide-react"; // Import Icons
import "./login.css";

export default function AccountantLogin({ setUser }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        usernameOrEmail: email.trim(),
        password: password.trim()
      };

      const response = await api.post("/auth/login", payload);

      if (response.data.role.toLowerCase() !== "accountant") {
        alert("Unauthorized role for accountant portal");
        return;
      }

      const sessionData = {
        userId: response.data.userId,
        role: response.data.role,
        email: response.data.email,
        username: response.data.username
      };

      localStorage.setItem("user_session", JSON.stringify(sessionData));
      setUser(sessionData);
      navigate("/accountant/dashboard");

    } catch (err) {
      console.error("Login error:", err);
      alert(err.response?.data?.message || "Login failed");
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <div className="auth-logo">NAST</div>
          <h2>Accountant Portal</h2>
          <p>Manage financial records and payroll</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label>Email or Username</label>
            <input
              type="text"
              placeholder="Enter username"
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
                type="button"
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

          <button type="submit" className="btn-primary">
            Sign In
          </button>
        </form>

        <div className="auth-footer">
          <button onClick={() => navigate("/")} className="btn-text">
            ← Back to Landing Page
          </button>
          <button onClick={() => navigate("/accountant/forgot-password")} className="btn-text">
            Forgot Password?
          </button>
        </div>
      </div>
    </div>
  );
}