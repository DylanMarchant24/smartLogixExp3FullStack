import React, { useState, useEffect, useCallback } from 'react';
import { getCupones, crearCupon, desactivarCupon, validarCupon } from '../services/api';
import Spinner from '../components/Spinner';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';
import './PageStyles.css';

function Cupones() {
  const [cupones, setCupones] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [modal, setModal] = useState(false);
  const [modalValidar, setModalValidar] = useState(false);
  const [resultadoValidacion, setResultadoValidacion] = useState(null);
  const [form, setForm] = useState({ codigo: '', tipo: 'PORCENTAJE', valor: '', fechaInicio: '', fechaFin: '' });
  const [formValidar, setFormValidar] = useState({ codigo: '', montoCompra: '' });
  const [saving, setSaving] = useState(false);
  const { toasts, success, error } = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    try { setCupones(await getCupones()); }
    catch (e) { error(e.message); }
    finally { setLoading(false); }
  }, [error]);

  useEffect(() => { load(); }, [load]);

  const filtered = cupones.filter((c) => c.codigo?.toLowerCase().includes(search.toLowerCase()));

  async function handleCrear(e) {
    e.preventDefault();
    setSaving(true);
    try {
      await crearCupon({ ...form, valor: Number(form.valor) });
      success('Cupón creado correctamente');
      setModal(false);
      setForm({ codigo: '', tipo: 'PORCENTAJE', valor: '', fechaInicio: '', fechaFin: '' });
      load();
    } catch (e) { error(e.message); }
    finally { setSaving(false); }
  }

  async function handleDesactivar(cupon) {
    if (!window.confirm(`¿Desactivar el cupón "${cupon.codigo}"?`)) return;
    try {
      await desactivarCupon(cupon.id);
      success('Cupón desactivado');
      load();
    } catch (e) { error(e.message); }
  }

  async function handleValidar(e) {
    e.preventDefault();
    setSaving(true);
    try {
      const resultado = await validarCupon({ ...formValidar, montoCompra: Number(formValidar.montoCompra) });
      setResultadoValidacion(resultado);
    } catch (e) { error(e.message); }
    finally { setSaving(false); }
  }

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));
  const setV = (k) => (e) => setFormValidar((f) => ({ ...f, [k]: e.target.value }));

  if (loading) return <Spinner message="Cargando cupones…" />;

  return (
    <div className="page">
      <Toast toasts={toasts} />

      <div className="page-toolbar">
        <input className="input" style={{ maxWidth: 280 }}
          placeholder="🔍 Buscar por código…"
          value={search} onChange={(e) => setSearch(e.target.value)} />
        <div style={{ display: 'flex', gap: 10 }}>
          <button className="btn btn-secondary" onClick={() => { setResultadoValidacion(null); setModalValidar(true); }}>
            🔎 Validar Cupón
          </button>
          <button className="btn btn-primary" onClick={() => setModal(true)}>
            + Nuevo Cupón
          </button>
        </div>
      </div>

      <div className="card">
        <div className="card-header">
          <span className="card-title">🏷️ Cupones de Descuento</span>
          <span className="badge badge-default">{filtered.length} registros</span>
        </div>
        <div style={{ overflowX: 'auto' }}>
          {filtered.length === 0 ? (
            <div className="empty-state">
              <div className="icon">🏷️</div>
              <p>No hay cupones registrados.</p>
            </div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Código</th><th>Tipo</th><th>Valor</th>
                  <th>Vigencia</th><th>Estado</th><th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((c) => (
                  <tr key={c.id}>
                    <td><code className="sku-chip">{c.codigo}</code></td>
                    <td>
                      <span className="badge badge-info">
                        {c.tipo === 'PORCENTAJE' ? 'Porcentaje' : 'Monto Fijo'}
                      </span>
                    </td>
                    <td>{c.tipo === 'PORCENTAJE' ? `${c.valor}%` : `$${Number(c.valor).toLocaleString('es-CL')}`}</td>
                    <td style={{ fontSize: '.82rem', color: 'var(--text-secondary)' }}>
                      {c.fechaInicio} → {c.fechaFin}
                    </td>
                    <td>
                      <span className={`badge ${c.activo ? 'badge-success' : 'badge-danger'}`}>
                        {c.activo ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>
                    <td>
                      {c.activo && (
                        <button className="btn btn-danger btn-sm" onClick={() => handleDesactivar(c)}>
                          Desactivar
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
            <h3>🏷️ Nuevo Cupón</h3>
            <form onSubmit={handleCrear}>
              <div className="form-group">
                <label>Código *</label>
                <input className="input" required value={form.codigo} onChange={set('codigo')} placeholder="Ej: VERANO2026" />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Tipo de Descuento *</label>
                  <select className="input" value={form.tipo} onChange={set('tipo')}>
                    <option value="PORCENTAJE">Porcentaje</option>
                    <option value="MONTO_FIJO">Monto Fijo</option>
                  </select>
                </div>
                <div className="form-group">
                  <label>Valor *</label>
                  <input className="input" type="number" min="0" step="0.01" required value={form.valor}
                    onChange={set('valor')} placeholder={form.tipo === 'PORCENTAJE' ? 'Ej: 15 (máx 100)' : 'Ej: 5000'} />
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Fecha Inicio *</label>
                  <input className="input" type="date" required value={form.fechaInicio} onChange={set('fechaInicio')} />
                </div>
                <div className="form-group">
                  <label>Fecha Fin *</label>
                  <input className="input" type="date" required value={form.fechaFin} onChange={set('fechaFin')} />
                </div>
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setModal(false)}>Cancelar</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Creando…' : 'Crear Cupón'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {modalValidar && (
        <div className="modal-overlay" onClick={() => setModalValidar(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>🔎 Validar Cupón</h3>
            <form onSubmit={handleValidar}>
              <div className="form-group">
                <label>Código del Cupón *</label>
                <input className="input" required value={formValidar.codigo} onChange={setV('codigo')} placeholder="Ej: VERANO2026" />
              </div>
              <div className="form-group">
                <label>Monto de Compra *</label>
                <input className="input" type="number" min="0" step="0.01" required value={formValidar.montoCompra}
                  onChange={setV('montoCompra')} placeholder="Ej: 50000" />
              </div>

              {resultadoValidacion && (
                <div className={resultadoValidacion.valido ? 'login-alert' : 'login-error'} style={{ marginTop: 12 }}>
                  {resultadoValidacion.valido ? (
                    <div>
                      <strong>✅ Cupón válido</strong>
                      <p>Rebaja: ${Number(resultadoValidacion.montoRebaja).toLocaleString('es-CL')}</p>
                      <p>Monto final: ${Number(resultadoValidacion.montoFinal).toLocaleString('es-CL')}</p>
                    </div>
                  ) : (
                    <div>
                      <strong>❌ Cupón no válido</strong>
                      <p>{resultadoValidacion.motivoRechazo}</p>
                    </div>
                  )}
                </div>
              )}

              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setModalValidar(false)}>Cerrar</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Validando…' : 'Validar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Cupones;
