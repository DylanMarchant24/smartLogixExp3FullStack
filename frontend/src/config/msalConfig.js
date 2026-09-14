import { PublicClientApplication, LogLevel } from '@azure/msal-browser';

/**
 * Configuración OIDC de MSAL (Microsoft Authentication Library) para React.
 * Lee las variables de entorno REACT_APP_AZURE_CLIENT_ID, REACT_APP_AZURE_TENANT_ID y REACT_APP_AZURE_REDIRECT_URI.
 */
const clientId = process.env.REACT_APP_AZURE_CLIENT_ID || '00000000-0000-0000-0000-000000000000';
const tenantId = process.env.REACT_APP_AZURE_TENANT_ID || 'common';
const redirectUri = process.env.REACT_APP_AZURE_REDIRECT_URI || (typeof window !== 'undefined' ? window.location.origin : 'http://localhost:3000');

export const msalConfig = {
  auth: {
    clientId: clientId,
    authority: `https://login.microsoftonline.com/${tenantId}`,
    redirectUri: redirectUri,
    postLogoutRedirectUri: redirectUri,
    navigateToLoginRequestUrl: true,
  },
  cache: {
    cacheLocation: 'sessionStorage',
    storeAuthStateInCookie: false,
  },
  system: {
    loggerOptions: {
      loggerCallback: (level, message, containsPii) => {
        if (containsPii) return;
        switch (level) {
          case LogLevel.Error:
            console.error(message);
            return;
          case LogLevel.Info:
            console.info(message);
            return;
          case LogLevel.Verbose:
            console.debug(message);
            return;
          case LogLevel.Warning:
            console.warn(message);
            return;
          default:
            return;
        }
      },
    },
  },
};

export const loginRequest = {
  scopes: ['api://56b5d583-bb7e-4030-a655-3834ed761639/api.access', 'openid', 'profile', 'email'],
};

export const msalInstance = new PublicClientApplication(msalConfig);
