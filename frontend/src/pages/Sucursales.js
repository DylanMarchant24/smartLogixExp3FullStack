import React, { useState, useEffect, useCallback } from 'react';

import {
  getSucursales,
  crearSucursal,
  actualizarSucursal,
  cambiarEstadoSucursal
} from '../services/api';

import Spinner from '../components/Spinner';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';

import './PageStyles.css';


const TIPOS = [
  'TIENDA_FISICA',
  'BODEGA_CENTRAL',
  'PUNTO_RETIRO'
];


function Sucursales() {

  const [sucursales, setSucursales] = useState([]);
  const [loading, setLoading] = useState(true);

  const [search, setSearch] = useState('');
  const [filtroTipo, setFiltroTipo] = useState('');
  const [filtroEstado, setFiltroEstado] = useState('');

  const [modal, setModal] = useState(null);
  const [selected, setSelected] = useState(null);

  const [form, setForm] = useState(emptyForm());
  const [saving, setSaving] = useState(false);

  const { toasts, success, error } = useToast();


  // ─────────────────────────────────────────
  // CARGAR SUCURSALES
  // ─────────────────────────────────────────

  const load = useCallback(async () => {

    setLoading(true);

    try {

      const data = await getSucursales();
      setSucursales(data);

    } catch (e) {

      error(e.message);

    } finally {

      setLoading(false);

    }

  }, [error]);


  useEffect(() => {

    load();

  }, [load]);


  // ─────────────────────────────────────────
  // FILTROS
  // ─────────────────────────────────────────

  const filtered = sucursales.filter((s) => {

    const texto = search.toLowerCase();

    const matchSearch =
      !search ||
      s.nombre?.toLowerCase().includes(texto) ||
      s.codigo?.toLowerCase().includes(texto) ||
      s.comuna?.toLowerCase().includes(texto) ||
      s.ciudad?.toLowerCase().includes(texto);


    const matchTipo =
      !filtroTipo ||
      s.tipo === filtroTipo;


    const matchEstado =
      filtroEstado === ''
        ? true
        : filtroEstado === 'ACTIVA'
          ? s.activo
          : !s.activo;


    return matchSearch && matchTipo && matchEstado;

  });


  // ─────────────────────────────────────────
  // ESTADÍSTICAS
  // ─────────────────────────────────────────

  const total = sucursales.length;

  const activas =
    sucursales.filter((s) => s.activo).length;

  const tiendas =
    sucursales.filter(
      (s) => s.tipo === 'TIENDA_FISICA'
    ).length;

  const bodegas =
    sucursales.filter(
      (s) => s.tipo === 'BODEGA_CENTRAL'
    ).length;

  const puntosRetiro =
    sucursales.filter(
      (s) => s.tipo === 'PUNTO_RETIRO'
    ).length;


  // ─────────────────────────────────────────
  // ABRIR CREAR
  // ─────────────────────────────────────────

  function openCrear() {

    setSelected(null);
    setForm(emptyForm());
    setModal('crear');

  }


  // ─────────────────────────────────────────
  // ABRIR EDITAR
  // ─────────────────────────────────────────

  function openEditar(sucursal) {

    setSelected(sucursal);

    setForm({
      codigo: sucursal.codigo || '',
      nombre: sucursal.nombre || '',
      direccion: sucursal.direccion || '',
      comuna: sucursal.comuna || '',
      ciudad: sucursal.ciudad || '',
      telefono: sucursal.telefono || '',
      horarioAtencion: sucursal.horarioAtencion || '',
      tipo: sucursal.tipo || 'TIENDA_FISICA'
    });

    setModal('editar');

  }


  // ─────────────────────────────────────────
  // CREAR / EDITAR
  // ─────────────────────────────────────────

  async function handleSubmit(e) {

    e.preventDefault();

    setSaving(true);

    try {

      if (modal === 'crear') {

        await crearSucursal(form);

        success('Sucursal creada correctamente');

      } else {

        await actualizarSucursal(
          selected.id,
          form
        );

        success('Sucursal actualizada correctamente');

      }

      setModal(null);

      load();

    } catch (e) {

      error(e.message);

    } finally {

      setSaving(false);

    }

  }


  // ─────────────────────────────────────────
  // CAMBIAR ESTADO
  // ─────────────────────────────────────────

  async function handleEstado(sucursal) {

    const nuevoEstado = !sucursal.activo;

    const accion = nuevoEstado
      ? 'activar'
      : 'desactivar';


    if (
      !window.confirm(
        `¿Seguro que deseas ${accion} la sucursal "${sucursal.nombre}"?`
      )
    ) {
      return;
    }


    try {

      await cambiarEstadoSucursal(
        sucursal.id,
        nuevoEstado
      );

      success(
        nuevoEstado
          ? 'Sucursal activada correctamente'
          : 'Sucursal desactivada correctamente'
      );

      load();

    } catch (e) {

      error(e.message);

    }

  }


  // ─────────────────────────────────────────
  // CONTROL FORMULARIO
  // ─────────────────────────────────────────

  const set = (campo) => (e) => {

    setForm((prev) => ({
      ...prev,
      [campo]: e.target.value
    }));

  };


  // ─────────────────────────────────────────
  // CARGANDO
  // ─────────────────────────────────────────

  if (loading) {

    return (
      <Spinner message="Cargando sucursales…" />
    );

  }


  // ─────────────────────────────────────────
  // RENDER
  // ─────────────────────────────────────────

  return (

    <div className="page">

      <Toast toasts={toasts} />


      {/* ESTADÍSTICAS */}

      <div className="sucursales-stats">

        <StatBox
          icon="🏢"
          label="Total"
          value={total}
        />

        <StatBox
          icon="🟢"
          label="Activas"
          value={activas}
        />

        <StatBox
          icon="🏪"
          label="Tiendas"
          value={tiendas}
        />

        <StatBox
          icon="📦"
          label="Bodegas"
          value={bodegas}
        />

        <StatBox
          icon="📍"
          label="Puntos de retiro"
          value={puntosRetiro}
        />

      </div>


      {/* TOOLBAR */}

      <div className="page-toolbar sucursales-toolbar">

        <input
          className="input"
          placeholder="🔍 Buscar por nombre, código, comuna o ciudad…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />


        <select
          className="input filter-select"
          value={filtroTipo}
          onChange={(e) =>
            setFiltroTipo(e.target.value)
          }
        >

          <option value="">
            Todos los tipos
          </option>

          <option value="TIENDA_FISICA">
            Tienda Física
          </option>

          <option value="BODEGA_CENTRAL">
            Bodega Central
          </option>

          <option value="PUNTO_RETIRO">
            Punto de Retiro
          </option>

        </select>


        <select
          className="input filter-select"
          value={filtroEstado}
          onChange={(e) =>
            setFiltroEstado(e.target.value)
          }
        >

          <option value="">
            Todos los estados
          </option>

          <option value="ACTIVA">
            Activas
          </option>

          <option value="INACTIVA">
            Inactivas
          </option>

        </select>


        <button
          className="btn btn-primary"
          onClick={openCrear}
        >

          + Nueva Sucursal

        </button>

      </div>


      {/* TABLA */}

      <div className="card">

        <div className="card-header">

          <span className="card-title">
            🏪 Gestión de Sucursales
          </span>

          <span className="badge badge-default">
            {filtered.length} registros
          </span>

        </div>


        <div style={{ overflowX: 'auto' }}>

          {filtered.length === 0 ? (

            <div className="empty-state">

              <div className="icon">
                🏪
              </div>

              <p>
                No se encontraron sucursales.
              </p>

            </div>

          ) : (

            <table>

              <thead>

                <tr>

                  <th>Código</th>
                  <th>Sucursal</th>
                  <th>Ubicación</th>
                  <th>Contacto</th>
                  <th>Tipo</th>
                  <th>Estado</th>
                  <th>Acciones</th>

                </tr>

              </thead>


              <tbody>

                {filtered.map((sucursal) => (

                  <tr key={sucursal.id}>


                    <td>

                      <code className="sku-chip">
                        {sucursal.codigo}
                      </code>

                    </td>


                    <td>

                      <strong>
                        {sucursal.nombre}
                      </strong>

                      <div className="table-secondary">

                        ID #{sucursal.id}

                      </div>

                    </td>


                    <td>

                      <strong>
                        {sucursal.comuna}
                      </strong>

                      <div className="table-secondary">

                        {sucursal.ciudad}

                      </div>

                    </td>


                    <td>

                      <div>
                        {sucursal.telefono || 'Sin teléfono'}
                      </div>

                      <div className="table-secondary">

                        {sucursal.horarioAtencion || 'Horario no informado'}

                      </div>

                    </td>


                    <td>

                      <TipoBadge
                        tipo={sucursal.tipo}
                      />

                    </td>


                    <td>

                      <span
                        className={`badge ${
                          sucursal.activo
                            ? 'badge-success'
                            : 'badge-danger'
                        }`}
                      >

                        {sucursal.activo
                          ? '● ACTIVA'
                          : '● INACTIVA'}

                      </span>

                    </td>


                    <td>

                      <div className="action-btns">

                        <button
                          className="btn btn-secondary btn-sm"
                          onClick={() =>
                            openEditar(sucursal)
                          }
                        >

                          ✏️ Editar

                        </button>


                        <button
                          className={`btn btn-sm ${
                            sucursal.activo
                              ? 'btn-danger'
                              : 'btn-primary'
                          }`}
                          onClick={() =>
                            handleEstado(sucursal)
                          }
                        >

                          {sucursal.activo
                            ? 'Desactivar'
                            : 'Activar'}

                        </button>

                      </div>

                    </td>

                  </tr>

                ))}

              </tbody>

            </table>

          )}

        </div>

      </div>


      {/* MODAL */}

      {modal && (

        <div
          className="modal-overlay"
          onClick={() => setModal(null)}
        >

          <div
            className="modal modal-large"
            onClick={(e) =>
              e.stopPropagation()
            }
          >

            <h3>

              {modal === 'crear'
                ? '🏪 Nueva Sucursal'
                : '✏️ Editar Sucursal'}

            </h3>


            <form onSubmit={handleSubmit}>


              <div className="form-row">

                <div className="form-group">

                  <label>
                    Código *
                  </label>

                  <input
                    className="input"
                    required
                    value={form.codigo}
                    onChange={set('codigo')}
                    placeholder="Ej: SUC-001"
                  />

                </div>


                <div className="form-group">

                  <label>
                    Tipo *
                  </label>

                  <select
                    className="input"
                    value={form.tipo}
                    onChange={set('tipo')}
                  >

                    {TIPOS.map((tipo) => (

                      <option
                        key={tipo}
                        value={tipo}
                      >

                        {formatTipo(tipo)}

                      </option>

                    ))}

                  </select>

                </div>

              </div>


              <div className="form-group">

                <label>
                  Nombre *
                </label>

                <input
                  className="input"
                  required
                  value={form.nombre}
                  onChange={set('nombre')}
                  placeholder="Ej: SmartLogix Santiago Centro"
                />

              </div>


              <div className="form-group">

                <label>
                  Dirección *
                </label>

                <input
                  className="input"
                  required
                  value={form.direccion}
                  onChange={set('direccion')}
                  placeholder="Ej: Av. Libertador Bernardo O'Higgins 123"
                />

              </div>


              <div className="form-row">

                <div className="form-group">

                  <label>
                    Comuna *
                  </label>

                  <input
                    className="input"
                    required
                    value={form.comuna}
                    onChange={set('comuna')}
                    placeholder="Ej: Santiago"
                  />

                </div>


                <div className="form-group">

                  <label>
                    Ciudad *
                  </label>

                  <input
                    className="input"
                    required
                    value={form.ciudad}
                    onChange={set('ciudad')}
                    placeholder="Ej: Santiago"
                  />

                </div>

              </div>


              <div className="form-row">

                <div className="form-group">

                  <label>
                    Teléfono
                  </label>

                  <input
                    className="input"
                    value={form.telefono}
                    onChange={set('telefono')}
                    placeholder="+56 9 1234 5678"
                  />

                </div>


                <div className="form-group">

                  <label>
                    Horario de atención
                  </label>

                  <input
                    className="input"
                    value={form.horarioAtencion}
                    onChange={set('horarioAtencion')}
                    placeholder="Lun-Vie 09:00 - 18:00"
                  />

                </div>

              </div>


              <div className="modal-actions">

                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() =>
                    setModal(null)
                  }
                >

                  Cancelar

                </button>


                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={saving}
                >

                  {saving
                    ? 'Guardando…'
                    : modal === 'crear'
                      ? 'Crear Sucursal'
                      : 'Guardar Cambios'}

                </button>

              </div>

            </form>

          </div>

        </div>

      )}

    </div>

  );

}


// ─────────────────────────────────────────
// COMPONENTE ESTADÍSTICA
// ─────────────────────────────────────────

function StatBox({ icon, label, value }) {

  return (

    <div className="sucursal-stat">

      <span className="sucursal-stat-icon">
        {icon}
      </span>

      <div>

        <div className="sucursal-stat-value">
          {value}
        </div>

        <div className="sucursal-stat-label">
          {label}
        </div>

      </div>

    </div>

  );

}


// ─────────────────────────────────────────
// BADGE TIPO
// ─────────────────────────────────────────

function TipoBadge({ tipo }) {

  const config = {

    TIENDA_FISICA: {
      icon: '🏪',
      label: 'Tienda Física',
      className: 'badge-primary'
    },

    BODEGA_CENTRAL: {
      icon: '📦',
      label: 'Bodega Central',
      className: 'badge-warning'
    },

    PUNTO_RETIRO: {
      icon: '📍',
      label: 'Punto de Retiro',
      className: 'badge-info'
    }

  };


  const item = config[tipo] || {
    icon: '🏢',
    label: tipo,
    className: 'badge-default'
  };


  return (

    <span
      className={`badge ${item.className}`}
    >

      {item.icon} {item.label}

    </span>

  );

}


// ─────────────────────────────────────────
// FORM VACÍO
// ─────────────────────────────────────────

function emptyForm() {

  return {

    codigo: '',
    nombre: '',
    direccion: '',
    comuna: '',
    ciudad: '',
    telefono: '',
    horarioAtencion: '',
    tipo: 'TIENDA_FISICA'

  };

}


function formatTipo(tipo) {

  const tipos = {

    TIENDA_FISICA: 'Tienda Física',
    BODEGA_CENTRAL: 'Bodega Central',
    PUNTO_RETIRO: 'Punto de Retiro'

  };

  return tipos[tipo] || tipo;

}


export default Sucursales;