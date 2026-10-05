import React from 'react';
import ReactDOM from 'react-dom/client';
import { MsalProvider } from '@azure/msal-react';
import { msalInstance } from './config/msalConfig';
import { saveSession } from './services/auth';
import './index.css';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));

msalInstance.initialize().then(() => {
  return msalInstance.handleRedirectPromise();
}).then((response) => {
  if (response && response.accessToken) {
    saveSession(response.accessToken, response.account?.name || response.account?.username || 'Usuario Azure');
  }
  root.render(
    <React.StrictMode>
      <MsalProvider instance={msalInstance}>
        <App />
      </MsalProvider>
    </React.StrictMode>
  );
}).catch((err) => {
  console.error('Error al inicializar MSAL:', err);
  root.render(
    <React.StrictMode>
      <MsalProvider instance={msalInstance}>
        <App />
      </MsalProvider>
    </React.StrictMode>
  );
});

