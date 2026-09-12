import React from 'react';
import ReactDOM from 'react-dom/client';
import { MsalProvider } from '@azure/msal-react';
import { msalInstance } from './config/msalConfig';
import './index.css';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));

msalInstance.initialize().then(() => {
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

