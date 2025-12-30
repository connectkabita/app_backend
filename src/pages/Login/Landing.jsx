import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './login.css';

const Landing = ({ setUser }) => {
    const [credentials, setCredentials] = useState({ username: '', password: '' });
    const [error, setError] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    // Handler for Login Form
    const handleLogin = async (e) => {
        e.preventDefault(); // CRITICAL: Stops the page refresh
        setIsLoading(true);
        setError('');

        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', credentials);
            const userData = response.data; 

            localStorage.setItem("user_session", JSON.stringify(userData));
            setUser(userData);

            // Redirect based on Role IDs (1=Admin, 3=Accountant, 4=Employee)
            const role = userData.role.toUpperCase();
            if (role === 'ROLE_ADMIN' || role === 'ADMIN') navigate('/admin/dashboard');
            else if (role === 'ROLE_ACCOUNTANT' || role === 'ACCOUNTANT') navigate('/accountant/dashboard');
            else if (role === 'ROLE_EMPLOYEE' || role === 'EMPLOYEE') navigate('/employee/dashboard');
            else setError("Access Denied: Role not recognized.");

        } catch (err) {
            setError(err.response?.status === 401 ? "Bad credentials" : "Server Connection Error");
        } finally {
            setIsLoading(false);
        }
    };

    // Handler for Trouble Signing In Button
    const handleTrouble = () => {
        navigate('/forgot-password'); // Redirects to the forgot password route
    };

    return (
        <div className="login-wrapper">
            <div className="login-card">
                <div className="login-header">
                    <h1>NAST</h1>
                    <p>Payroll Management System</p>
                    <span className="badge">SECURE GATEWAY</span>
                </div>

                <form onSubmit={handleLogin} className="login-form">
                    <div className="input-group">
                        <label>USERNAME</label>
                        <input 
                            type="text" 
                            placeholder="Enter your username" 
                            required 
                            onChange={(e) => setCredentials({...credentials, username: e.target.value})} 
                        />
                    </div>

                    <div className="input-group">
                        <label>PASSWORD</label>
                        <input 
                            type="password" 
                            placeholder="••••••••" 
                            required 
                            onChange={(e) => setCredentials({...credentials, password: e.target.value})} 
                        />
                    </div>

                    {error && <div className="error-box">{error}</div>}

                    <button type="submit" className="login-btn" disabled={isLoading}>
                        {isLoading ? 'VERIFYING...' : 'SIGN IN'}
                    </button>
                </form>

                {/* Fixed functional button */}
                <button 
                    type="button" 
                    className="trouble-link" 
                    onClick={handleTrouble}
                >
                    Trouble signing in?
                </button>
            </div>
        </div>
    );
};

export default Landing;