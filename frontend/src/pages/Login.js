import React, { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useMsal, useIsAuthenticated } from '@azure/msal-react';
import { loginRequest } from '../config/msalConfig';
import { isAuthenticated, saveSession } from '../services/auth';
import './Login.css';

function Login() {
  const { instance, inProgress } = useMsal();
  const msalIsAuthenticated = useIsAuthenticated();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const navigate = useNavigate();
  const location = useLocation();

  if (msalIsAuthenticated || isAuthenticated()) {
    return <Navigate to="/dashboard" replace />;
  }

  const expired = new URLSearchParams(location.search).get('expired') === 'true';

  const handleMicrosoftLogin = async (usePopup = true) => {
    setLoading(true);
    setError('');

    try {
      if (usePopup) {
        const response = await instance.loginPopup(loginRequest);
        if (response && response.accessToken) {
          saveSession(response.accessToken, response.account?.name || response.account?.username || 'Usuario Azure');
        }
        navigate('/dashboard', { replace: true });
      } else {
        await instance.loginRedirect(loginRequest);
      }
    } catch (err) {
      console.error('Error durante autenticación con Microsoft Entra ID:', err);
      setError(err.message || 'No se pudo iniciar sesión con Microsoft Entra ID');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-brand">
          <span className="login-icon">⚡</span>
          <div>
            <h1>SmartLogix</h1>
            <p className="login-subtitle">Autenticación OIDC con Microsoft Entra ID</p>
          </div>
        </div>

        {expired && (
          <div className="login-alert">
            Tu sesión expiró. Inicia sesión nuevamente.
          </div>
        )}

        {error && <div className="login-error">{error}</div>}

        <div className="login-actions">
          <button
            className="login-button ms-login-btn"
            onClick={() => handleMicrosoftLogin(true)}
            disabled={loading || inProgress === 'login'}
          >
            <svg className="ms-logo-icon" viewBox="0 0 23 23" width="20" height="20">
              <path fill="#f35325" d="M1 1h10v10H1z"/>
              <path fill="#81bc06" d="M12 1h10v10H12z"/>
              <path fill="#05a6f0" d="M1 12h10v10H1z"/>
              <path fill="#ffba08" d="M12 12h10v10H12z"/>
            </svg>
            <span>{loading ? 'Validando con Microsoft…' : 'Iniciar sesión con Microsoft Entra ID'}</span>
          </button>

          <button
            className="login-button-secondary"
            onClick={() => handleMicrosoftLogin(false)}
            disabled={loading || inProgress === 'login'}
          >
            Iniciar sesión por Redirección
          </button>
        </div>

        <p className="login-help">
          Autenticación segura federada vía Azure AD / Microsoft Entra ID OIDC.
        </p>
      </div>
    </div>
  );
}

export default Login;