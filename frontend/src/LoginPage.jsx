import React, { useState } from 'react';
import './assets/styles.css';
import { postJson } from './api';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  const handleSignIn = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    try {
      const data = await postJson('/api/auth/login', { username, password }, { credentials: 'include' });
      if (data.role !== 'CUSTOMER' && data.role !== 'ADMIN') {
        throw new Error('Login succeeded, but the backend returned an unsupported user role.');
      }
      setSuccess('Login successful.');
    } catch (err) {
      console.error('Login failed', err);
      setError(err.message || 'Login failed');
    }
  };

  return (
    <div className="page-container">
      <div className="form-container">
        <h1 className="form-title">Login</h1>
        {error && <p className="error-message">{error}</p>}
        {success && <p className="success-message">{success}</p>}
        <form onSubmit={handleSignIn} className="form-content">
          <div className="form-group">
            <label htmlFor="username" className="form-label">Username</label>
            <input
              id="username"
              type="text"
              placeholder="Enter your username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              className="form-input"
            />
          </div>
          <div className="form-group">
            <label htmlFor="password" className="form-label">Password</label>
            <input
              id="password"
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="form-input"
            />
          </div>
          <button type="submit" className="form-button">Sign In</button>
        </form>
        <div className="form-footer">
          <a href="/register" className="form-link">New User? Sign up here</a>
        </div>
      </div>
    </div>
  );
}