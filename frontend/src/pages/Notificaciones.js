import React, { useState, useEffect, useCallback } from 'react';
import { getNotificaciones, crearNotificacion, reenviarNotificacion } from '../services/api';
import Spinner from '../components/Spinner';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';
import './PageStyles.css';

const ESTADOS = ['PENDIENTE', 'ENVIADO', 'FALLIDO'];

const BADGE_MAP = {
  PENDIENTE: 'badge-warning',
  ENVIADO: 'badge-success',
  FALLIDO: 'badge-danger',
};

function Notificaciones() {
  const [notificaciones, setNotificaciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [filtroEstado, setFiltroEstado] = useState('');
  const [modal, setModal] = useState(false);
  const [form, setForm] = useState({ destinatario: '', tipo: 'EMAIL', asunto: '', mensaje: '' });
  const [saving, setSaving] = useState(false);
  const { toasts, success, error } = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    try { setNotificaciones(await getNotificaciones()); }
    catch (e) { error(e.message); }
    finally { setLoading(false); }
  }, [error]);

  useEffect(() => { load(); }, [load]);

  const filtered = notificaciones.filter((n) => {
    const matchSearch = n.destinatario?.toLowerCase().includes(search.toLowerCase()) ||
                        n.asunto?.toLowerCase().includes(search.toLowerCase());
    const matchEstado = filtroEstado ? n.estado === filtroEstado : true;
    return matchSearch && matchEstado;
  });

  async function handleCrear(e) {
    e.preventDefault();
    setSaving(true);
    try {
      await crearNotificacion(form);
      success('Notificación creada y enviada (simulado)');
      setModal(false);
      setForm({ destinatario: '', tipo: 'EMAIL', asunto: '', mensaje: '' });
      load();
    } catch (e) { error(e.message); }
    finally { setSaving(false); }
  }

  async function handleReenviar(n) {
    try {
      await reenviarNotificacion(n.id);
      success('Notificación reenviada');
      load();
    } catch (e) { error(e.message); }
  }

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  if (loading) return <Spinner message="Cargando notificaciones…" />;

  return (
    <div className="page">
      <Toast toasts={toasts} />

      <div className="page-toolbar">
        <div style={{ display: 'flex', gap: 10, flex: 1, flexWrap: 'wrap' }}>
          <input className="input" style={{ maxWidth: 280 }}
            placeholder="🔍 Buscar por destinatario o asunto…"
            value={search} onChange={(e) => setSearch(e.target.value)} />
          <select className="input" style={{ maxWidth: 180 }}
            value={filtroEstado} onChange={(e) => setFiltroEstado(e.target.value)}>
            <option value="">Todos los estados</option>
            {ESTADOS.map((es) => <option key={es} value={es}>{es}</option>)}
          </select>
        </div>
        <button className="btn btn-primary" onClick={() => setModal(true)}>
          + Nueva Notificación
        </button>
      </div>

      <div className="card">
        <div className="card-header">
          <span className="card-title">🔔 Historial de Notificaciones</span>
          <span className="badge badge-default">{filtered.length} registros</span>
        </div>
        <div style={{ overflowX: 'auto' }}>
          {filtered.length === 0 ? (
            <div className="empty-state">
              <div className="icon">🔔</div>
              <p>No hay notificaciones registradas.</p>
            </div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>#</th><th>Destinatario</th><th>Tipo</th>
                  <th>Asunto</th><th>Estado</th><th>Fecha</th><th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((n) => (
                  <tr key={n.id}>
                    <td><strong>#{n.id}</strong></td>
                    <td>{n.destinatario}</td>
                    <td>
                      <span className="badge badge-info">
                        {n.tipo === 'EMAIL' ? '📧 Email' : '⚠️ Alerta'}
                      </span>
                    </td>
                    <td style={{ maxWidth: 220, overflow: 'hidden', textOverflow: 'ellipsis' }}>{n.asunto}</td>
                    <td>
                      <span className={`badge ${BADGE_MAP[n.estado] || 'badge-default'}`}>{n.estado}</span>
                    </td>
                    <td style={{ fontSize: '.8rem', color: 'var(--text-muted)' }}>
                      {n.fechaCreacion ? new Date(n.fechaCreacion).toLocaleString('es-CL') : '–'}
                    </td>
                    <td>
                      {n.estado === 'FALLIDO' && (
                        <button className="btn btn-secondary btn-sm" onClick={() => handleReenviar(n)}>
                          ↻ Reenviar
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {modal && (
        <div className="modal-overlay" onClick={() => setModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>🔔 Nueva Notificación</h3>
            <p style={{ fontSize: '.82rem', color: 'var(--text-secondary)', marginBottom: 18 }}>
              El envío es simulado: se registra en el log del servidor, sin conexión SMTP real.
            </p>
            <form onSubmit={handleCrear}>
              <div className="form-row">
                <div className="form-group">
                  <label>Destinatario *</label>
                  <input className="input" required value={form.destinatario} onChange={set('destinatario')}
                    placeholder="correo@cliente.cl" />
                </div>
                <div className="form-group">
                  <label>Tipo *</label>
                  <select className="input" value={form.tipo} onChange={set('tipo')}>
                    <option value="EMAIL">Email</option>
                    <option value="ALERTA">Alerta</option>
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label>Asunto *</label>
                <input className="input" required value={form.asunto} onChange={set('asunto')}
                  placeholder="Ej: Confirmación de pedido" />
              </div>
              <div className="form-group">
                <label>Mensaje *</label>
                <input className="input" required value={form.mensaje} onChange={set('mensaje')}
                  placeholder="Contenido del mensaje" />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setModal(false)}>Cancelar</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Enviando…' : 'Crear y Enviar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Notificaciones;
