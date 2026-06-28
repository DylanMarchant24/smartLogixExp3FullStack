import React, { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { login } from '../services/api';
import { isAuthenticated } from '../services/auth';
import './Login.css';

function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const navigate = useNavigate();

  if (isAuthenticated()) {
    return <Navigate to="/dashboard" replace />;
  }

  async function handleSubmit(e) {
    e.preventDefault();

    setLoading(true);
    setError('');

    try {
      await login(username, password);
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError(err.message || 'No se pudo iniciar sesión');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-brand">
          <span className="login-icon">⚡</span>
          <h1>SmartLogix</h1>
        </div>


        {error && <div className="login-error">{error}</div>}

        <form onSubmit={handleSubmit}>
          <label>Usuario</label>
          <input
            className="login-input"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Ingresa tu usuario"
            autoComplete="username"
            required
          />

          <label>Contraseña</label>
          <input
            className="login-input"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Ingresa tu contraseña"
            autoComplete="current-password"
            required
          />

          <button className="login-button" type="submit" disabled={loading}>
            {loading ? 'Validando…' : 'Iniciar sesión'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default Login;