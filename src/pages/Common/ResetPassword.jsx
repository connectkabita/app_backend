import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../Login/login.css"; 

const ResetPassword = () => {
    const [formData, setFormData] = useState({ token: "", newPassword: "", confirmPassword: "" });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const handleReset = async (e) => {
        e.preventDefault();
        if (formData.newPassword !== formData.confirmPassword) {
            setError("Passwords do not match!");
            return;
        }

        setLoading(true);
        try {
            // Matches @PostMapping("/reset-password") in UserController
            await axios.post("http://localhost:8080/api/users/reset-password", null, {
                params: { 
                    token: formData.token, 
                    newPassword: formData.newPassword 
                }
            });
            alert("Success! Password updated.");
            navigate("/"); // Go back to login
        } catch (err) {
            setError("Invalid OTP or server error.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-wrapper">
            <div className="login-card">
                <h1>NAST</h1>
                <h3>Set New Password</h3>
                {error && <div className="error-msg" style={{color: 'red'}}>{error}</div>}
                <form onSubmit={handleReset}>
                    <div className="input-group" style={{textAlign: 'left'}}>
                        <label>6-DIGIT OTP</label>
                        <input type="text" placeholder="Enter OTP" onChange={(e) => setFormData({...formData, token: e.target.value})} required />
                    </div>
                    <div className="input-group" style={{textAlign: 'left'}}>
                        <label>NEW PASSWORD</label>
                        <input type="password" placeholder="••••••••" onChange={(e) => setFormData({...formData, newPassword: e.target.value})} required />
                    </div>
                    <div className="input-group" style={{textAlign: 'left'}}>
                        <label>CONFIRM PASSWORD</label>
                        <input type="password" placeholder="••••••••" onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})} required />
                    </div>
                    <button type="submit" className="login-btn" disabled={loading}>
                        {loading ? "UPDATING..." : "Update Password"}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ResetPassword;