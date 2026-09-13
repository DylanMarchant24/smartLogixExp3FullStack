import axios from 'axios';
import { clearSession, getToken } from './auth';
import { msalInstance, loginRequest } from '../config/msalConfig';

/**
 * PATRÓN: Service Layer (Frontend Multicloud)
 * Centraliza las llamadas HTTP apuntando al BFF (puerto 8080 en local o AWS API Gateway).
 * Adquiere tokens OIDC de Azure MSAL automáticamente para cada petición.
 * Todos los dominios (inventario, pedidos, envíos, proveedores, calificaciones,
 * cupones, notificaciones, pagos, sucursales, usuarios) pasan exclusivamente por el BFF.
 */

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://127.0.0.1:8080';
const BASE = API_BASE_URL.replace(/\/$/, '') + (API_BASE_URL.includes('/api/bff') ? '' : '/api/bff');

const api = axios.create({
  baseURL: BASE,
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
});

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

// Interceptor JWT: adquiere token desde MSAL e inserta Authorization: Bearer <accessToken>
api.interceptors.request.use(async (config) => {
  try {
    const token = await getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  } catch (e) {
    // Continuar petición sin header si falla obtención de token
  }
  return config;
});

// ── Interceptores de Respuesta ────────────────────────────────────────
// NUNCA usar window.location.href en interceptores HTTP para evitar recargas en bucle (parpadeos).
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.status === 401) {
      clearSession();
      const msg = error?.response?.data?.error || 'Sesión expirada o token no autorizado.';
      return Promise.reject(new Error(msg));
    }
    const msg = error?.response?.data?.error || error.message || 'Error de conexión con el servidor';
    return Promise.reject(new Error(msg));
  }
);

// ── Dashboard ────────────────────────────────────────────────────────────────
export const getDashboard = () => api.get('/dashboard').then((r) => r.data);

// ── Inventario ────────────────────────────────────────────────────────────────
export const getInventario   = ()        => api.get('/inventario').then((r) => r.data);
export const crearProducto   = (data)    => api.post('/inventario', data).then((r) => r.data);
export const actualizarProducto = (id, data) =>
  api.put(`/inventario/${id}`, data).then((r) => r.data);
export const eliminarProducto = (id)     => api.delete(`/inventario/${id}`).then((r) => r.data);

// ── Pedidos ─────────────────────────────────────────────────────────────────────
export const getPedidos    = ()     => api.get('/pedidos').then((r) => r.data);
export const crearPedido   = (data) => api.post('/pedidos', data).then((r) => r.data);
export const cambiarEstado = (id, estado) =>
  api.patch(`/pedidos/${id}/estado`, { estado }).then((r) => r.data);

// ── Envíos ─────────────────────────────────────────────────────────────────────
export const getEnvios       = ()     => api.get('/envios').then((r) => r.data);
export const crearEnvio      = (data) => api.post('/envios', data).then((r) => r.data);
export const actualizarEnvio = (id, estado) =>
  api.patch(`/envios/${id}/estado`, { estado }).then((r) => r.data);

// ── Pagos ────────────────────────────────────────────────────────────────────
export const getPagos = () => api.get('/pagos').then((r) => r.data);
export const procesarPago = (data) => api.post('/pagos/procesar', data).then((r) => r.data);
export const getPagosPorPedido = (pedidoId) =>
  api.get(`/pagos/pedido/${pedidoId}`).then((r) => r.data);

// ── Sucursales ──────────────────────────────────────────────────────────────────
export const getSucursales = () => api.get('/sucursales').then((r) => r.data);
export const getSucursalesActivas = () => api.get('/sucursales/activas').then((r) => r.data);
export const crearSucursal = (data) => api.post('/sucursales', data).then((r) => r.data);
export const actualizarSucursal = (id, data) => api.put(`/sucursales/${id}`, data).then((r) => r.data);
export const cambiarEstadoSucursal = (id, activo) =>
  api.patch(`/sucursales/${id}/estado`, { activo }).then((r) => r.data);

// ── Usuarios ────────────────────────────────────────────────────────────────
export const getUsuarios = () => api.get('/usuarios').then((r) => r.data);

// ── Proveedores ──────────────────────────────────────────────────────────────
export const getProveedores = () => api.get('/proveedores').then((r) => r.data);
export const getProveedoresActivos = () => api.get('/proveedores/activos').then((r) => r.data);
export const createProveedor = (data) => api.post('/proveedores', data).then((r) => r.data);
export const updateProveedor = (id, data) => api.put(`/proveedores/${id}`, data).then((r) => r.data);
export const desactivarProveedor = (id) => api.patch(`/proveedores/${id}/desactivar`).then((r) => r.data);

// ── Calificaciones ──────────────────────────────────────────────────────────
export const getCalificaciones = () => api.get('/calificaciones').then((r) => r.data);
export const getCalificacionesPorProducto = (productoId) =>
  api.get(`/calificaciones/producto/${productoId}`).then((r) => r.data);
export const getPromedioCalificacion = (productoId) =>
  api.get(`/calificaciones/producto/${productoId}/promedio`).then((r) => r.data);
export const crearCalificacion = (data) => api.post('/calificaciones', data).then((r) => r.data);

// ── Cupones ──────────────────────────────────────────────────────────────────
export const getCupones = () => api.get('/cupones').then((r) => r.data);
export const getCuponPorId = (id) => api.get(`/cupones/${id}`).then((r) => r.data);
export const crearCupon = (data) => api.post('/cupones', data).then((r) => r.data);
export const desactivarCupon = (id) => api.patch(`/cupones/${id}/desactivar`).then((r) => r.data);
export const validarCupon = (data) => api.post('/cupones/validar', data).then((r) => r.data);

// ── Notificaciones ──────────────────────────────────────────────────────────
export const getNotificaciones = () => api.get('/notificaciones').then((r) => r.data);
export const getNotificacionPorId = (id) => api.get(`/notificaciones/${id}`).then((r) => r.data);
export const getNotificacionesPorDestinatario = (destinatario) =>
  api.get(`/notificaciones/destinatario/${destinatario}`).then((r) => r.data);
export const getNotificacionesPorEstado = (estado) =>
  api.get(`/notificaciones/estado/${estado}`).then((r) => r.data);
export const crearNotificacion = (data) => api.post('/notificaciones', data).then((r) => r.data);
export const reenviarNotificacion = (id) => api.post(`/notificaciones/${id}/reenviar`).then((r) => r.data);

export default api;
