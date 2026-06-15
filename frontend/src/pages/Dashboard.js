import React from 'react';
import StatCard from '../components/StatCard';
import Spinner  from '../components/Spinner';
import { useDashboard } from '../hooks/useDashboard';
import './Dashboard.css';

/**
 * Dashboard – página de inicio de SmartLogix.
 * Consume el hook useDashboard que llama al BFF (/api/bff/dashboard)
 * y obtiene datos combinados de inventario + pedidos + envíos.
 */
function Dashboard() {
  const { data, loading, error, refresh } = useDashboard();

  if (loading) return <Spinner message="Cargando dashboard…" />;

  if (error) {
    return (
      <div className="error-banner">
        <span>⚠️</span>
        <div>
          <strong>No se pudo conectar al servidor</strong>
          <p>Verifica que el BFF y los microservicios estén activos.</p>
          <p className="error-detail">{error}</p>
        </div>
        <button className="btn btn-secondary btn-sm" onClick={refresh}>
          Reintentar
        </button>
      </div>
    );
  }

  const r = data?.resumen || {};

  return (
    <div className="dashboard">
      {/* ── Encabezado ── */}
      <div className="dashboard-header">
        <div>
          <h2 className="dashboard-title">Resumen Operacional</h2>
          <p className="dashboard-subtitle">Vista en tiempo real del sistema logístico</p>
        </div>
        <button className="btn btn-secondary btn-sm" onClick={refresh}>
          🔄 Actualizar
        </button>
      </div>

      {/* ── KPIs ── */}
      <div className="stats-grid">
        <StatCard title="Productos en inventario" value={r.totalProductos}  icon="📦" color="blue"   sub="Total de SKUs registrados" />
        <StatCard title="Pedidos totales"          value={r.totalPedidos}    icon="🛒" color="purple" sub="Todos los estados" />
        <StatCard title="Pedidos aprobados"        value={r.pedidosAprobados} icon="✅" color="green" sub="Stock descontado OK" />
        <StatCard title="Pedidos pendientes"       value={r.pedidosPendientes} icon="⏳" color="orange" sub="En espera de validación" />
        <StatCard title="Envíos totales"           value={r.totalEnvios}     icon="🚚" color="blue"   sub="Despachos creados" />
        <StatCard title="Envíos pendientes"        value={r.enviosPendientes} icon="🕐" color="red"   sub="Sin despachar" />
      </div>

      {/* ── Tablas resumen ── */}
      <div className="dashboard-grid">
        {/* Últimos pedidos */}
        <div className="card">
          <div className="card-header">
            <span className="card-title">🛒 Últimos Pedidos</span>
          </div>
          <div style={{ overflowX: 'auto' }}>
            {(!data?.pedidos || data.pedidos.length === 0) ? (
              <div className="empty-state">
                <div className="icon">🛒</div>
                <p>No hay pedidos registrados</p>
              </div>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>#</th>
                    <th>SKU</th>
                    <th>Cantidad</th>
                    <th>Cliente</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {data.pedidos.slice(0, 5).map((p) => (
                    <tr key={p.id}>
                      <td><strong>#{p.id}</strong></td>
                      <td><code style={{ background:'var(--surface2)', padding:'2px 6px', borderRadius:4 }}>{p.skuProducto}</code></td>
                      <td>{p.cantidad}</td>
                      <td>{p.clienteEmail}</td>
                      <td><EstadoBadge estado={p.estado} /></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>

        {/* Stock crítico */}
        <div className="card">
          <div className="card-header">
            <span className="card-title">⚠️ Stock Crítico</span>
          </div>
          <div style={{ overflowX: 'auto' }}>
            {(!data?.productos || data.productos.length === 0) ? (
              <div className="empty-state">
                <div className="icon">📦</div>
                <p>No hay productos registrados</p>
              </div>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>Producto</th>
                    <th>SKU</th>
                    <th>Stock</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {data.productos
                    .sort((a, b) => a.stock - b.stock)
                    .slice(0, 5)
                    .map((p) => (
                      <tr key={p.id}>
                        <td><strong>{p.nombre}</strong></td>
                        <td><code style={{ background:'var(--surface2)', padding:'2px 6px', borderRadius:4 }}>{p.sku}</code></td>
                        <td>
                          <span style={{ fontWeight: 600, color: p.stock < 10 ? 'var(--danger)' : p.stock < 20 ? 'var(--warning)' : 'var(--success)' }}>
                            {p.stock}
                          </span>
                        </td>
                        <td>
                          <span className={`badge ${p.stock < 10 ? 'badge-danger' : p.stock < 20 ? 'badge-warning' : 'badge-success'}`}>
                            {p.stock < 10 ? 'Crítico' : p.stock < 20 ? 'Bajo' : 'OK'}
                          </span>
                        </td>
                      </tr>
                    ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

function EstadoBadge({ estado }) {
  const map = {
    APROBADO: 'badge-success', VALIDADO: 'badge-info',
    CREADO: 'badge-default',   RECHAZADO: 'badge-danger',
    DESPACHADO: 'badge-primary',
  };
  return <span className={`badge ${map[estado] || 'badge-default'}`}>{estado}</span>;
}

export default Dashboard;
