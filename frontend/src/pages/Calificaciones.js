import React, { useState, useEffect, useCallback } from 'react';
import { getCalificaciones, crearCalificacion } from '../services/api';
import Spinner from '../components/Spinner';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';
import './PageStyles.css';

function Calificaciones() {
  const [calificaciones, setCalificaciones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [filtroPuntuacion, setFiltroPuntuacion] = useState('');
  const [modal, setModal] = useState(false);
  const [form, setForm] = useState({ productoId: '', clienteNombre: '', puntuacion: 5, comentario: '' });
  const [saving, setSaving] = useState(false);
  const { toasts, success, error } = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    try { setCalificaciones(await getCalificaciones()); }
    catch (e) { error(e.message); }
    finally { setLoading(false); }
  }, [error]);

  useEffect(() => { load(); }, [load]);

  const filtered = calificaciones.filter((c) => {
    const matchSearch = c.clienteNombre?.toLowerCase().includes(search.toLowerCase()) ||
                        String(c.productoId)?.includes(search) ||
                        c.comentario?.toLowerCase().includes(search.toLowerCase());
    const matchPuntuacion = filtroPuntuacion ? c.puntuacion === Number(filtroPuntuacion) : true;
    return matchSearch && matchPuntuacion;
  });

  const promedioGeneral = calificaciones.length
    ? (calificaciones.reduce((sum, c) => sum + c.puntuacion, 0) / calificaciones.length).toFixed(2)
    : '0.00';

  async function handleCrear(e) {
    e.preventDefault();
    setSaving(true);
    try {
      await crearCalificacion({ ...form, productoId: Number(form.productoId), puntuacion: Number(form.puntuacion) });
      success('Calificación registrada correctamente');
      setModal(false);
      setForm({ productoId: '', clienteNombre: '', puntuacion: 5, comentario: '' });
      load();
    } catch (e) { error(e.message); }
    finally { setSaving(false); }
  }

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  if (loading) return <Spinner message="Cargando calificaciones…" />;

  return (
    <div className="page">
      <Toast toasts={toasts} />

      <div className="page-toolbar">
        <div style={{ display: 'flex', gap: 10, flex: 1, flexWrap: 'wrap' }}>
          <input className="input" style={{ maxWidth: 300 }}
            placeholder="🔍 Buscar por cliente, producto o comentario…"
            value={search} onChange={(e) => setSearch(e.target.value)} />
          <select className="input" style={{ maxWidth: 160 }}
            value={filtroPuntuacion} onChange={(e) => setFiltroPuntuacion(e.target.value)}>
            <option value="">Todas las puntuaciones</option>
            {[5, 4, 3, 2, 1].map((p) => <option key={p} value={p}>{p} ⭐</option>)}
          </select>
        </div>
        <button className="btn btn-primary" onClick={() => setModal(true)}>
          + Nueva Calificación
        </button>
      </div>

      <div className="stats-grid" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))' }}>
        <div className="card" style={{ padding: '18px 20px' }}>
          <p style={{ fontSize: '.78rem', fontWeight: 600, color: 'var(--text-secondary)', textTransform: 'uppercase' }}>Promedio general</p>
          <p style={{ fontSize: '1.8rem', fontWeight: 700 }}>{promedioGeneral} ⭐</p>
        </div>
        <div className="card" style={{ padding: '18px 20px' }}>
          <p style={{ fontSize: '.78rem', fontWeight: 600, color: 'var(--text-secondary)', textTransform: 'uppercase' }}>Total reseñas</p>
          <p style={{ fontSize: '1.8rem', fontWeight: 700 }}>{calificaciones.length}</p>
        </div>
      </div>

      <div className="card">
        <div className="card-header">
          <span className="card-title">⭐ Calificaciones y Reseñas</span>
          <span className="badge badge-default">{filtered.length} registros</span>
        </div>
        <div style={{ overflowX: 'auto' }}>
          {filtered.length === 0 ? (
            <div className="empty-state">
              <div className="icon">⭐</div>
              <p>No hay calificaciones registradas.</p>
            </div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>#</th><th>Producto</th><th>Cliente</th>
                  <th>Puntuación</th><th>Comentario</th><th>Fecha</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((c) => (
                  <tr key={c.id}>
                    <td><strong>#{c.id}</strong></td>
                    <td>#{c.productoId}</td>
                    <td>{c.clienteNombre}</td>
                    <td>
                      <span className="badge badge-warning">
                        {'⭐'.repeat(c.puntuacion)}
                      </span>
                    </td>
                    <td style={{ maxWidth: 240, color: 'var(--text-secondary)' }}>
                      {c.comentario || '—'}
                    </td>
                    <td style={{ fontSize: '.8rem', color: 'var(--text-muted)' }}>
                      {c.fechaCreacion ? new Date(c.fechaCreacion).toLocaleDateString('es-CL') : '–'}
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
            <h3>⭐ Nueva Calificación</h3>
            <form onSubmit={handleCrear}>
              <div className="form-row">
                <div className="form-group">
                  <label>ID del Producto *</label>
                  <input className="input" type="number" min="1" required value={form.productoId}
                    onChange={set('productoId')} placeholder="Ej: 5" />
                </div>
                <div className="form-group">
                  <label>Puntuación *</label>
                  <select className="input" value={form.puntuacion} onChange={set('puntuacion')}>
                    {[5, 4, 3, 2, 1].map((p) => <option key={p} value={p}>{p} ⭐</option>)}
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label>Nombre del Cliente *</label>
                <input className="input" required value={form.clienteNombre}
                  onChange={set('clienteNombre')} placeholder="Ej: Juan Pérez" />
              </div>
              <div className="form-group">
                <label>Comentario</label>
                <input className="input" maxLength={255} value={form.comentario}
                  onChange={set('comentario')} placeholder="Comentario sobre el producto (máx. 255 caracteres)" />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setModal(false)}>Cancelar</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Guardando…' : 'Registrar Calificación'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Calificaciones;
