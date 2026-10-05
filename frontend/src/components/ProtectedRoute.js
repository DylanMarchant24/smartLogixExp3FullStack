import React from 'react';
import { Navigate } from 'react-router-dom';
import { useIsAuthenticated, useMsal } from '@azure/msal-react';
import { isAuthenticated } from '../services/auth';

function ProtectedRoute({ children }) {
  const msalIsAuthenticated = useIsAuthenticated();
  const { inProgress } = useMsal();

  if (inProgress !== 'none') {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', fontFamily: 'sans-serif' }}>
        <p>Iniciando sesión en SmartLogix...</p>
      </div>
    );
  }

  if (!msalIsAuthenticated && !isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

export default ProtectedRoute;