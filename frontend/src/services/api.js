import axios from 'axios';
import { clearSession, getToken } from './auth';
import { msalInstance, loginRequest } from '../config/msalConfig';

/**
 * PATRÓN: Service Layer (Frontend Multicloud)
 * Centraliza las llamadas HTTP apuntando al AWS API Gateway.
 * Adquiere tokens OIDC de Azure MSAL automáticamente para cada petición.
 */

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://127.0.0.1:8085';
const BASE = API_BASE_URL.replace(/\/$/, '') + (API_BASE_URL.includes('/api/bff') ? '' : '/api/bff');

const api = axios.create({
  baseURL: BASE,
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
});

/**
 * Adquiere el token de acceso desde MSAL de forma asíncrona (o fallback a localStorage).
 */
export const getAccessToken = async () => {
  try {
    const accounts = msalInstance.getAllAccounts();
    if (accounts.length > 0) {
      const response = await msalInstance.acquireTokenSilent({
        ...loginRequest,
        account: accounts[0],
      });
      if (response && response.accessToken) {
        return response.accessToken;
      }
    }
  } catch (error) {
    console.warn('MSAL silent token acquisition failed, fallback to stored token:', error);
  }
  return getToken();
};

// Interceptor JWT: adquiere token desde MSAL e inserta Authorization: Bearer <accessToken>
api.interceptors.request.use(async (config) => {
  const token = await getAccessToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

// ── Interceptores de Respuesta ────────────────────────────────────────────────
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.status === 401) {
      clearSession();

      if (window.location.pathname !== '/login') {
        window.location.href = '/login?expired=true';
      }

      return Promise.reject(new Error('Sesión expirada o no autorizada. Inicia sesión nuevamente.'));
    }

    const msg = error?.response?.data?.error || error.message || 'Error de conexión';
    return Promise.reject(new Error(msg));
  }
);

// ── Dashboard ────────────────────────────────────────────────────────────────
export const getDashboard = () => api.get('/dashboard').then((r) => r.data);

// ── Inventario ───────────────────────────────────────────────────────────────
export const getInventario   = ()        => api.get('/inventario').then((r) => r.data);
export const crearProducto   = (data)    => api.post('/inventario', data).then((r) => r.data);
export const actualizarProducto = (id, data) =>
  api.put(`/inventario/${id}`, data).then((r) => r.data);
export const eliminarProducto = (id)     => api.delete(`/inventario/${id}`).then((r) => r.data);

// ── Pedidos ──────────────────────────────────────────────────────────────────
export const getPedidos    = ()     => api.get('/pedidos').then((r) => r.data);
export const crearPedido   = (data) => api.post('/pedidos', data).then((r) => r.data);
export const cambiarEstado = (id, estado) =>
  api.patch(`/pedidos/${id}/estado`, { estado }).then((r) => r.data);

// ── Envíos ───────────────────────────────────────────────────────────────────
export const getEnvios       = ()     => api.get('/envios').then((r) => r.data);
export const crearEnvio      = (data) => api.post('/envios', data).then((r) => r.data);
export const actualizarEnvio = (id, estado) =>
  api.patch(`/envios/${id}/estado`, { estado }).then((r) => r.data);

export default api;
