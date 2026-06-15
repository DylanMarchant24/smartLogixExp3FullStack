import React, { useState, useEffect, useCallback } from 'react';
import { getPedidos, crearPedido, cambiarEstado } from '../services/api';
import Spinner from '../components/Spinner';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';
import './PageStyles.css';

const ESTADOS = ['CREADO', 'VALIDADO', 'APROBADO', 'RECHAZADO', 'DESPACHADO'];

const BADGE_MAP = {
  APROBADO:   'badge-success',
  VALIDADO:   'badge-info',
  CREADO:     'badge-default',
  RECHAZADO:  'badge-danger',
  DESPACHADO: 'badge-primary',
};

function Pedidos() {
  const [pedidos,  setPedidos]  = useState([]);
  const [loading,  setLoading]  = useState(true);
  const [search,   setSearch]   = useState('');
  const [filtroEs, setFiltroEs] = useState('');
  const [modal,    setModal]    = useState(false);
  const [form,     setForm]     = useState({ skuProducto: '', cantidad: 1, clienteEmail: '' });
  const [saving,   setSaving]   = useState(false);
  const { toasts, success, error } = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    try { setPedidos(await getPedidos()); }
    catch (e) { error(e.message); }
    finally { setLoading(false); }
  }, [error]);

  useEffect(() => { load(); }, [load]);

  const filtered = pedidos.filter((p) => {
    const matchSearch = p.skuProducto?.toLowerCase().includes(search.toLowerCase()) ||
                        p.clienteEmail?.toLowerCase().includes(search.toLowerCase());
    const matchEstado = filtroEs ? p.estado === filtroEs : true;
    return matchSearch && matchEstado;
  });

  async function handleCrear(e) {
    e.preventDefault();
    setSaving(true);
    try {
      await crearPedido({ ...form, cantidad: Number(form.cantidad) });
      success('Pedido creado – stock validado con ms-inventario');
      setModal(false);
      setForm({ skuProducto: '', cantidad: 1, clienteEmail: '' });
      load();
    } catch (e) { error(e.message); }
    finally { setSaving(false); }
  }

  async function handleCambioEstado(id, nuevoEstado) {
    try {
      await cambiarEstado(id, nuevoEstado);
      success(`Estado actualizado a ${nuevoEstado}`);
      load();
    } catch (e) { error(e.message); }
  }

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  if (loading) return <Spinner message="Cargando pedidos…" />;

  return (
    <div className="page">
      <Toast toasts={toasts} />

      {/* ── Toolbar ── */}
      <div className="page-toolbar">
        <div style={{ display: 'flex', gap: 10, flex: 1, flexWrap: 'wrap' }}>
          <input className="input" style={{ maxWidth: 260 }}
            placeholder="🔍 Buscar por SKU o email…"
            value={search} onChange={(e) => setSearch(e.target.value)} />
          <select className="input" style={{ maxWidth: 180 }}
            value={filtroEs} onChange={(e) => setFiltroEs(e.target.value)}>
            <option value="">Todos los estados</option>
            {ESTADOS.map((es) => <option key={es} value={es}>{es}</option>)}
          </select>
        </div>
        <button className="btn btn-primary" onClick={() => setModal(true)}>
          + Nuevo Pedido
        </button>
      </div>

      {/* ── Tabla ── */}
      <div className="card">
        <div className="card-header">
          <span className="card-title">🛒 Gestión de Pedidos</span>
          <span className="badge badge-default">{filtered.length} registros</span>
        </div>
        <div style={{ overflowX: 'auto' }}>
          {filtered.length === 0 ? (
            <div className="empty-state">
              <div className="icon">🛒</div>
              <p>No hay pedidos que coincidan con la búsqueda.</p>
            </div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>#</th><th>SKU</th><th>Cantidad</th>
                  <th>Cliente</th><th>Estado</th><th>Stock ref.</th><th>Cambiar estado</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((p) => (
                  <tr key={p.id}>
                    <td><strong>#{p.id}</strong></td>
                    <td><code className="sku-chip">{p.skuProducto}</code></td>
                    <td>{p.cantidad}</td>
                    <td>{p.clienteEmail}</td>
                    <td><span className={`badge ${BADGE_MAP[p.estado] || 'badge-default'}`}>{p.estado}</span></td>
                    <td style={{ color: 'var(--text-secondary)' }}>
                      {p.ultimoStockConocido != null ? p.ultimoStockConocido : '–'}
                    </td>
                    <td>
                      <select className="input" style={{ fontSize: '.78rem', padding: '5px 8px' }}
                        value={p.estado}
                        onChange={(e) => handleCambioEstado(p.id, e.target.value)}>
                        {ESTADOS.map((es) => <option key={es} value={es}>{es}</option>)}
                      </select>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {/* ── Modal nuevo pedido ── */}
      {modal && (
        <div className="modal-overlay" onClick={() => setModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>🛒 Nuevo Pedido</h3>
            <p style={{ fontSize: '.82rem', color: 'var(--text-secondary)', marginBottom: 18 }}>
              El sistema validará el stock disponible en ms-inventario con Circuit Breaker.
            </p>
            <form onSubmit={handleCrear}>
              <div className="form-group">
                <label>SKU del Producto *</label>
                <input className="input" required value={form.skuProducto} onChange={set('skuProducto')}
                  placeholder="Ej: SKU-001" />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Cantidad *</label>
                  <input className="input" type="number" min="1" required value={form.cantidad} onChange={set('cantidad')} />
                </div>
                <div className="form-group">
                  <label>Email del Cliente *</label>
                  <input className="input" type="email" required value={form.clienteEmail} onChange={set('clienteEmail')}
                    placeholder="cliente@email.com" />
                </div>
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setModal(false)}>Cancelar</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Procesando…' : 'Crear Pedido'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Pedidos;
