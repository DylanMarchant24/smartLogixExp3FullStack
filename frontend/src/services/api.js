import axios from 'axios';
import { getToken, saveSession } from './auth';

/**
 * PATRÓN: Service Layer (Frontend)
 * Centraliza todas las llamadas HTTP al BFF en un único módulo.
 * El frontend nunca llama directamente a los microservicios;
 * siempre pasa por el BFF (puerto 8080).
 */

const BASE = '/api/bff';
const AUTH_BASE = '/api/auth';


const api = axios.create({
  baseURL: BASE,
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
});

const authApi = axios.create({
  baseURL: AUTH_BASE,
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
});

// Interceptor JWT: agrega Authorization: Bearer TOKEN
api.interceptors.request.use((config) => {
  const token = getToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

// ── Interceptores ────────────────────────────────────────────────────────────
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const msg = error?.response?.data?.error || error.message || 'Error de conexión';
    return Promise.reject(new Error(msg));
  }
);

// ── Autenticación ────────────────────────────────────────────────────────────
export const login = async (username, password) => {
  try {
    const response = await authApi.post('/login', { username, password });
    const { token } = response.data;

    saveSession(token, response.data.username || username);

    return response.data;
  } catch (error) {
    if (error?.response?.status === 401) {
      throw new Error('Usuario o contraseña incorrectos.');
    }

    if (error?.response?.data?.error) {
      throw new Error(error.response.data.error);
    }

    throw new Error('No se pudo conectar con el servidor de autenticación.');
  }
};

export const validarToken = () =>
  authApi.post('/validate', null, {
    headers: { Authorization: `Bearer ${getToken()}` },
  }).then((r) => r.data);

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
