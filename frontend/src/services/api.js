import axios from 'axios';
import { clearSession, getToken, saveSession } from './auth';

/**
 * PATRÓN: Service Layer (Frontend)
 * Centraliza todas las llamadas HTTP al BFF en un único módulo.
 * El frontend nunca llama directamente a los microservicios;
 * siempre pasa por el BFF (puerto 8080).
 */

const API_GATEWAY_URL = 'http://localhost:8085';

const BASE = `${API_GATEWAY_URL}/api/bff`;
const AUTH_BASE = `${API_GATEWAY_URL}/api/auth`;
const PAGOS_BASE = `${API_GATEWAY_URL}/api/pagos`;
const SUCURSALES_BASE = `${API_GATEWAY_URL}/api/sucursales`;
const USUARIOS_BASE = `${API_GATEWAY_URL}/api/usuarios`;

const defaultConfig = {
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
};

const api = axios.create({
  baseURL: BASE,
  ...defaultConfig,
});

const authApi = axios.create({
  baseURL: AUTH_BASE,
  ...defaultConfig,
});

const pagosApi = axios.create({
  baseURL: PAGOS_BASE,
  ...defaultConfig,
});

const sucursalesApi = axios.create({
  baseURL: SUCURSALES_BASE,
  ...defaultConfig,
});

const usuariosApi = axios.create({
  baseURL: USUARIOS_BASE,
  ...defaultConfig,
});

// Interceptor JWT: agrega Authorization: Bearer TOKEN
api.interceptors.request.use((config) => {
  const token = getToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

pagosApi.interceptors.request.use((config) => {
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
    if (error?.response?.status === 401) {
      clearSession();

      if (window.location.pathname !== '/login') {
        window.location.href = '/login?expired=true';
      }

      return Promise.reject(new Error('Sesión expirada. Inicia sesión nuevamente.'));
    }

    const msg = error?.response?.data?.error || error.message || 'Error de conexión';
    return Promise.reject(new Error(msg));
  }
);

usuariosApi.interceptors.request.use((config) => {
  const token = getToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

usuariosApi.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status;

    if (status === 401) {
      clearSession();

      if (window.location.pathname !== '/login') {
        window.location.href = '/login?expired=true';
      }

      return Promise.reject(
        new Error('Sesión expirada. Inicia sesión nuevamente.')
      );
    }

    const message =
      error?.response?.data?.message ||
      error?.response?.data?.error ||
      error?.message ||
      'No se pudieron cargar los usuarios.';

    return Promise.reject(new Error(message));
  }
);

sucursalesApi.interceptors.request.use((config) => {
  const token = getToken();

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

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

// ── Pagos ───────────────────────────────────────────────────────────────────
export const getPagos = () =>
  pagosApi.get('').then((r) => r.data);

export const procesarPago = (data) =>
  pagosApi.post('/procesar', data).then((r) => r.data);

export const getPagosPorPedido = (pedidoId) =>
  pagosApi.get(`/pedido/${pedidoId}`).then((r) => r.data);

// ── Usuarios
export const getUsuarios = async () => {
  const response = await usuariosApi.get('/');
  return response.data;
};

export const getUsuarioPorId = async (id) => {
  const response = await usuariosApi.get(`/${id}`);
  return response.data;
};

export const crearUsuario = async (data) => {
  const response = await usuariosApi.post('/', data);
  return response.data;
};

export const desactivarUsuario = async (id) => {
  const response = await usuariosApi.delete(`/${id}`);
  return response.data;
};
// ── Sucursales ───────────────────────────────────────────────────────

export const getSucursales = () =>
  sucursalesApi.get('').then((r) => r.data);

export const getSucursalesActivas = () =>
  sucursalesApi.get('/activas').then((r) => r.data);

export const crearSucursal = (data) =>
  sucursalesApi.post('', data).then((r) => r.data);

export const actualizarSucursal = (id, data) =>
  sucursalesApi.put(`/${id}`, data).then((r) => r.data);

export const cambiarEstadoSucursal = (id, activo) =>
  sucursalesApi.patch(`/${id}/estado`, { activo }).then((r) => r.data);

export default api;
