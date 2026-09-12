import { msalInstance } from '../config/msalConfig';

const TOKEN_KEY = 'smartlogix_jwt_token';
const USER_KEY = 'smartlogix_username';

export function saveSession(token, username) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USER_KEY, username);
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function getUsername() {
  const accounts = msalInstance.getAllAccounts();
  if (accounts.length > 0) {
    return accounts[0].name || accounts[0].username;
  }
  return localStorage.getItem(USER_KEY) || 'Usuario';
}

export function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
  const accounts = msalInstance.getAllAccounts();
  if (accounts.length > 0) {
    msalInstance.logoutPopup().catch((err) => console.warn('MSAL logout error:', err));
  }
}

export function isAuthenticated() {
  const accounts = msalInstance.getAllAccounts();
  return accounts.length > 0 || Boolean(getToken());
}