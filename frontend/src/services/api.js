import axios from 'axios';
import { clearSession, getToken, saveSession } from './auth';
import { msalInstance, loginRequest } from '../config/msalConfig';

/**
 * PATRÓN: Service Layer (Frontend Multicloud)
 * Centraliza las llamadas HTTP apuntando al BFF (puerto 8080 en local o AWS API Gateway).
 * Adquiere tokens OIDC de Azure MSAL automáticamente para cada petición.
 */

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://127.0.0.1:8080';
const BASE = API_BASE_URL.replace(/\/$/, '') + (API_BASE_URL.includes('/api/bff') ? '' : '/api/bff');

const AUTH_BASE = `${API_BASE_URL.replace(/\/$/, '')}/api/auth`;
const PAGOS_BASE = `${API_BASE_URL.replace(/\/$/, '')}/api/pagos`;
const SUCURSALES_BASE = `${API_BASE_URL.replace(/\/$/, '')}/api/sucursales`;
const USUARIOS_BASE = `${API_BASE_URL.replace(/\/$/, '')}/api/usuarios`;

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
    console.warn('MSAL silent token acquisition warning:', error);
  }
  return getToken();
};

// Helper para adjuntar Bearer token a las instancias de Axios
const addAuthHeader = async (config) => {
  try {
    const token = await getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  } catch (e) {
    // Continuar petición sin header si falla obtención de token
  }
  return config;
};

// Interceptores JWT: adquieren token desde MSAL o localStorage para cada cliente HTTP
api.interceptors.request.use(addAuthHeader);
pagosApi.interceptors.request.use(addAuthHeader);
sucursalesApi.interceptors.request.use(addAuthHeader);
usuariosApi.interceptors.request.use(addAuthHeader);

// ── Interceptores de Respuesta ────────────────────────────────────────────────
// NUNCA usar window.location.href en interceptores HTTP para evitar recargas en bucle (parpadeos).
const handleResponseError = (error, defaultMsg = 'Error de conexión con el servidor') => {
  if (error?.response?.status === 401) {
    clearSession();
    const msg = error?.response?.data?.error || error?.response?.data?.message || 'Sesión expirada o token no autorizado.';
    return Promise.reject(new Error(msg));
  }

  const msg = error?.response?.data?.message || error?.response?.data?.error || error?.message || defaultMsg;
  return Promise.reject(new Error(msg));
};

api.interceptors.response.use((response) => response, (error) => handleResponseError(error));
pagosApi.interceptors.response.use((response) => response, (error) => handleResponseError(error, 'No se pudieron procesar los pagos.'));
sucursalesApi.interceptors.response.use((response) => response, (error) => handleResponseError(error, 'No se pudieron cargar las sucursales.'));
usuariosApi.interceptors.response.use((response) => response, (error) => handleResponseError(error, 'No se pudieron cargar los usuarios.'));

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

export const validarToken = async () => {
  const token = await getAccessToken();
  return authApi.post('/validate', null, {
    headers: { Authorization: `Bearer ${token}` },
  }).then((r) => r.data);
};

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

// ── Proveedores ───────────────────────────────────────────────────────
export const getProveedores = async () => {
  const response = await api.get('/proveedores');
  return response.data;
};

export const createProveedor = async (proveedorData) => {
  const response = await api.post('/proveedores', proveedorData);
  return response.data;
};

export const updateProveedor = async (id, proveedorData) => {
  const response = await api.put(`/proveedores/${id}`, proveedorData);
  return response.data;
};

export const deleteProveedor = async (id) => {
  const response = await api.delete(`/proveedores/${id}`);
  return response.data;
};

export const toggleEstadoProveedor = async (id, estado) => {
  const response = await api.put(`/proveedores/${id}/estado`, { activo: estado });
  return response.data;
};

export default api;
