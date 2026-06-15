import React, { useState, useEffect, useCallback } from 'react';
import { getEnvios, crearEnvio, actualizarEnvio } from '../services/api';
import Spinner from '../components/Spinner';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';
import './PageStyles.css';

const ESTADOS_ENVIO = ['PENDIENTE', 'EN_CAMINO', 'ENTREGADO', 'FALLIDO'];

const BADGE_MAP = {
  PENDIENTE:  'badge-warning',
  EN_CAMINO:  'badge-info',
  ENTREGADO:  'badge-success',
  FALLIDO:    'badge-danger',
};

function Envios() {
  const [envios,   setEnvios]   = useState([]);
  const [loading,  setLoading]  = useState(true);
  const [search,   setSearch]   = useState('');
  const [filtroEs, setFiltroEs] = useState('');
  const [modal,    setModal]    = useState(false);
  const [form,     setForm]     = useState({ pedidoId: '', transportista: '', direccionDestino: '' });
  const [saving,   setSaving]   = useState(false);
  const { toasts, success, error } = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    try { setEnvios(await getEnvios()); }
    catch (e) { error(e.message); }
    finally { setLoading(false); }
  }, [error]);

  useEffect(() => { load(); }, [load]);

  const filtered = envios.filter((e) => {
    const matchSearch = String(e.pedidoId)?.includes(search) ||
                        e.codigoSeguimiento?.toLowerCase().includes(search.toLowerCase()) ||
                        e.transportista?.toLowerCase().includes(search.toLowerCase());
    const matchEstado = filtroEs ? e.estado === filtroEs : true;
    return matchSearch && matchEstado;
  });

  async function handleCrear(ev) {
    ev.preventDefault();
    setSaving(true);
    try {
      await crearEnvio({ ...form, pedidoId: Number(form.pedidoId) });
      success('Envío creado con código de seguimiento generado');
      setModal(false);
      setForm({ pedidoId: '', transportista: '', direccionDestino: '' });
      load();
    } catch (e) { error(e.message); }
    finally { setSaving(false); }
  }

  async function handleCambioEstado(id, nuevoEstado) {
    try {
      await actualizarEnvio(id, nuevoEstado);
      success(`Estado del envío actualizado a ${nuevoEstado}`);
      load();
    } catch (e) { error(e.message); }
  }

  const set = (k) => (ev) => setForm((f) => ({ ...f, [k]: ev.target.value }));

  if (loading) return <Spinner message="Cargando envíos…" />;

  return (
    <div className="page">
      <Toast toasts={toasts} />

      {/* ── Toolbar ── */}
      <div className="page-toolbar">
        <div style={{ display: 'flex', gap: 10, flex: 1, flexWrap: 'wrap' }}>
          <input className="input" style={{ maxWidth: 280 }}
            placeholder="🔍 Buscar por pedido, código o transportista…"
            value={search} onChange={(e) => setSearch(e.target.value)} />
          <select className="input" style={{ maxWidth: 180 }}
            value={filtroEs} onChange={(e) => setFiltroEs(e.target.value)}>
            <option value="">Todos los estados</option>
            {ESTADOS_ENVIO.map((es) => <option key={es} value={es}>{es}</option>)}
          </select>
        </div>
        <button className="btn btn-primary" onClick={() => setModal(true)}>
          + Nuevo Envío
        </button>
      </div>

      {/* ── Tabla ── */}
      <div className="card">
        <div className="card-header">
          <span className="card-title">🚚 Coordinación de Envíos</span>
          <span className="badge badge-default">{filtered.length} registros</span>
        </div>
        <div style={{ overflowX: 'auto' }}>
          {filtered.length === 0 ? (
            <div className="empty-state">
              <div className="icon">🚚</div>
              <p>No hay envíos registrados.</p>
            </div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>#</th><th>Pedido</th><th>Código Seguimiento</th>
                  <th>Transportista</th><th>Destino</th><th>Estado</th>
                  <th>Entrega</th><th>Cambiar estado</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((e) => (
                  <tr key={e.id}>
                    <td><strong>#{e.id}</strong></td>
                    <td>#{e.pedidoId}</td>
                    <td><code className="sku-chip">{e.codigoSeguimiento || '–'}</code></td>
                    <td>{e.transportista}</td>
                    <td style={{ maxWidth: 160, overflow: 'hidden', textOverflow: 'ellipsis',
                                 color: 'var(--text-secondary)', fontSize: '.82rem' }}>
                      {e.direccionDestino}
                    </td>
                    <td>
                      <span className={`badge ${BADGE_MAP[e.estado] || 'badge-default'}`}>
                        {e.estado?.replace('_', ' ')}
                      </span>
                    </td>
                    <td style={{ fontSize: '.8rem', color: 'var(--text-muted)' }}>
                      {e.fechaEntrega ? new Date(e.fechaEntrega).toLocaleDateString('es-CL') : '–'}
                    </td>
                    <td>
                      <select className="input" style={{ fontSize: '.78rem', padding: '5px 8px' }}
                        value={e.estado}
                        onChange={(ev) => handleCambioEstado(e.id, ev.target.value)}>
                        {ESTADOS_ENVIO.map((es) => <option key={es} value={es}>{es}</option>)}
                      </select>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {/* ── Modal nuevo envío ── */}
      {modal && (
        <div className="modal-overlay" onClick={() => setModal(false)}>
          <div className="modal" onClick={(ev) => ev.stopPropagation()}>
            <h3>🚚 Nuevo Envío</h3>
            <p style={{ fontSize: '.82rem', color: 'var(--text-secondary)', marginBottom: 18 }}>
              El sistema generará automáticamente el código de seguimiento.
            </p>
            <form onSubmit={handleCrear}>
              <div className="form-group">
                <label>ID del Pedido Aprobado *</label>
                <input className="input" type="number" min="1" required value={form.pedidoId}
                  onChange={set('pedidoId')} placeholder="Ej: 5" />
              </div>
              <div className="form-group">
                <label>Transportista *</label>
                <select className="input" required value={form.transportista} onChange={set('transportista')}>
                  <option value="">Seleccionar transportista…</option>
                  <option value="Chilexpress">Chilexpress</option>
                  <option value="Starken">Starken</option>
                  <option value="Blue Express">Blue Express</option>
                  <option value="Correos de Chile">Correos de Chile</option>
                  <option value="DHL">DHL</option>
                </select>
              </div>
              <div className="form-group">
                <label>Dirección de Destino *</label>
                <input className="input" required value={form.direccionDestino}
                  onChange={set('direccionDestino')} placeholder="Av. Providencia 1234, Santiago" />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setModal(false)}>Cancelar</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Creando…' : 'Crear Envío'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Envios;
