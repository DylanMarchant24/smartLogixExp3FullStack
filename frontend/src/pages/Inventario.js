import React, { useState, useEffect, useCallback } from 'react';
import { getInventario, crearProducto, actualizarProducto, eliminarProducto } from '../services/api';
import Spinner from '../components/Spinner';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';
import './PageStyles.css';

function Inventario() {
  const [productos, setProductos] = useState([]);
  const [loading,   setLoading]   = useState(true);
  const [search,    setSearch]    = useState('');
  const [modal,     setModal]     = useState(null); // null | 'crear' | 'editar'
  const [selected,  setSelected]  = useState(null);
  const [form,      setForm]      = useState(emptyForm());
  const [saving,    setSaving]    = useState(false);
  const { toasts, success, error } = useToast();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const data = await getInventario();
      setProductos(data);
    } catch (e) { error(e.message); }
    finally { setLoading(false); }
  }, [error]);

  useEffect(() => { load(); }, [load]);

  const filtered = productos.filter(
    (p) =>
      p.nombre?.toLowerCase().includes(search.toLowerCase()) ||
      p.sku?.toLowerCase().includes(search.toLowerCase())
  );

  function openCrear() { setForm(emptyForm()); setModal('crear'); }

  function openEditar(p) {
    setSelected(p);
    setForm({ nombre: p.nombre, sku: p.sku, stock: p.stock,
              precio: p.precio, descripcion: p.descripcion || '' });
    setModal('editar');
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSaving(true);
    try {
      if (modal === 'crear') {
        await crearProducto({ ...form, stock: Number(form.stock), precio: Number(form.precio) });
        success('Producto creado correctamente');
      } else {
        await actualizarProducto(selected.id, { ...form, stock: Number(form.stock), precio: Number(form.precio) });
        success('Producto actualizado correctamente');
      }
      setModal(null);
      load();
    } catch (e) { error(e.message); }
    finally { setSaving(false); }
  }

  async function handleEliminar(p) {
    if (!window.confirm(`¿Eliminar "${p.nombre}"?`)) return;
    try {
      await eliminarProducto(p.id);
      success('Producto eliminado');
      load();
    } catch (e) { error(e.message); }
  }

  const set = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  if (loading) return <Spinner message="Cargando inventario…" />;

  return (
    <div className="page">
      <Toast toasts={toasts} />

      {/* ── Toolbar ── */}
      <div className="page-toolbar">
        <input className="input" style={{ maxWidth: 280 }}
          placeholder="🔍 Buscar por nombre o SKU…"
          value={search} onChange={(e) => setSearch(e.target.value)} />
        <button className="btn btn-primary" onClick={openCrear}>
          + Nuevo Producto
        </button>
      </div>

      {/* ── Tabla ── */}
      <div className="card">
        <div className="card-header">
          <span className="card-title">📦 Inventario de Productos</span>
          <span className="badge badge-default">{filtered.length} registros</span>
        </div>
        <div style={{ overflowX: 'auto' }}>
          {filtered.length === 0 ? (
            <div className="empty-state">
              <div className="icon">📦</div>
              <p>No hay productos. Crea el primero.</p>
            </div>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>#</th><th>Nombre</th><th>SKU</th>
                  <th>Stock</th><th>Precio (CLP)</th><th>Descripción</th><th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((p) => (
                  <tr key={p.id}>
                    <td><strong>#{p.id}</strong></td>
                    <td><strong>{p.nombre}</strong></td>
                    <td><code className="sku-chip">{p.sku}</code></td>
                    <td>
                      <span style={{ fontWeight: 600, color: p.stock < 10 ? 'var(--danger)' : p.stock < 20 ? 'var(--warning)' : 'inherit' }}>
                        {p.stock}
                      </span>
                      {p.stock < 10 && <span className="badge badge-danger" style={{ marginLeft: 6 }}>Crítico</span>}
                    </td>
                    <td>${Number(p.precio).toLocaleString('es-CL')}</td>
                    <td style={{ color: 'var(--text-secondary)', maxWidth: 180, overflow: 'hidden', textOverflow: 'ellipsis' }}>
                      {p.descripcion || '–'}
                    </td>
                    <td>
                      <div className="action-btns">
                        <button className="btn btn-secondary btn-sm" onClick={() => openEditar(p)}>✏️ Editar</button>
                        <button className="btn btn-danger btn-sm"    onClick={() => handleEliminar(p)}>🗑️</button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {/* ── Modal ── */}
      {modal && (
        <div className="modal-overlay" onClick={() => setModal(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>{modal === 'crear' ? '➕ Nuevo Producto' : '✏️ Editar Producto'}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-row">
                <div className="form-group">
                  <label>Nombre *</label>
                  <input className="input" required value={form.nombre} onChange={set('nombre')} placeholder="Ej: Laptop Pro 15" />
                </div>
                <div className="form-group">
                  <label>SKU *</label>
                  <input className="input" required value={form.sku} onChange={set('sku')}
                    disabled={modal === 'editar'} placeholder="Ej: SKU-001" />
                </div>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Stock *</label>
                  <input className="input" type="number" min="0" required value={form.stock} onChange={set('stock')} />
                </div>
                <div className="form-group">
                  <label>Precio (CLP) *</label>
                  <input className="input" type="number" min="0" step="1" required value={form.precio} onChange={set('precio')} />
                </div>
              </div>
              <div className="form-group">
                <label>Descripción</label>
                <input className="input" value={form.descripcion} onChange={set('descripcion')} placeholder="Descripción breve del producto" />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setModal(null)}>Cancelar</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving ? 'Guardando…' : modal === 'crear' ? 'Crear Producto' : 'Guardar Cambios'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

function emptyForm() {
  return { nombre: '', sku: '', stock: 0, precio: 0, descripcion: '' };
}

export default Inventario;
