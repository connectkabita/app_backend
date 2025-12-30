import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../Login/login.css"; // Fixed path

const ForgotPassword = () => {
    const [email, setEmail] = useState("");
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState({ type: "", text: "" });
    const navigate = useNavigate();

    const handleResetRequest = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage({ type: "", text: "" });

        try {
            await axios.post("http://localhost:8080/api/users/forgot-password", null, {
                params: { email: email.trim() }
            });
            
            setMessage({ type: "success", text: "OTP sent! Redirecting to Reset Page..." });
            
            // Redirect to Reset Password page after 2 seconds
            setTimeout(() => {
                navigate("/reset-password"); 
            }, 2000);

        } catch (error) {
            if (error.response && error.response.status === 404) {
                setMessage({ type: "error", text: "Email not found in our records." });
            } else {
                setMessage({ type: "error", text: "Server connection failed." });
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-wrapper">
            <div className="login-card">
                <div className="login-header">
                    <h1>NAST</h1>
                    <p>Forgot Password</p>
                </div>

                {message.text && (
                    <div className={message.type === "error" ? "error-msg" : "success-msg"}>
                        {message.text}
                    </div>
                )}

                <form onSubmit={handleResetRequest}>
                    <div className="input-group" style={{ textAlign: "left" }}>
                        <label>REGISTERED EMAIL</label>
                        <input
                            type="email"
                            placeholder="e.g., kabitadha6@gmail.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>
                    <button type="submit" className="login-btn" disabled={loading}>
                        {loading ? "SENDING..." : "Send Reset Link"}
                    </button>
                </form>
                <button type="button" className="trouble-btn" onClick={() => navigate("/")}>
                    Back to Login
                </button>
            </div>
        </div>
    );
};

export default ForgotPassword;