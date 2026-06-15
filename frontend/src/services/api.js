import axios from 'axios';

/**
 * PATRÓN: Service Layer (Frontend)
 * Centraliza todas las llamadas HTTP al BFF en un único módulo.
 * El frontend nunca llama directamente a los microservicios;
 * siempre pasa por el BFF (puerto 8080).
 */

const BASE = '/api/bff';

const api = axios.create({
  baseURL: BASE,
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000,
});

// ── Interceptores ────────────────────────────────────────────────────────────
api.interceptors.response.use(
  (response) => response,
  (error) => {
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
