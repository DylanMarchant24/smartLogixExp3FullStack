import React, { useState, useEffect } from 'react';
import { 
  getProveedores, 
  createProveedor, 
  updateProveedor, 
  desactivarProveedor, 
  toggleEstadoProveedor 
} from '../services/api';
import Toast from '../components/Toast';
import Spinner from '../components/Spinner';
import './PageStyles.css';

const Proveedores = () => {
  const [proveedores, setProveedores] = useState([]);
  const [filteredProveedores, setFilteredProveedores] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  
  // Estado correcto para el componente Toast que espera un arreglo
  const [toast, setToast] = useState({ show: false, messages: [], type: 'info' });

  // Función adaptada para siempre enviar un arreglo a messages
  const showToast = (message, type = 'info') => {
    setToast({ 
      show: true, 
      messages: Array.isArray(message) ? message : [message], 
      type 
    });
  };

  const [formData, setFormData] = useState({
    nombre: '',
    rut: '',
    email: '',
    telefono: '',
    direccion: '',
    categoria: '',
    activo: true
  });

  useEffect(() => {
    fetchProveedores();
  }, []);

  useEffect(() => {
    const results = proveedores.filter(p => 
      p.nombre?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.rut?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      p.categoria?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setFilteredProveedores(results);
  }, [searchTerm, proveedores]);

  const fetchProveedores = async () => {
    try {
      setLoading(true);
      const data = await getProveedores();
      setProveedores(data);
      setFilteredProveedores(data);
    } catch (err) {
      showToast('Error al cargar proveedores desde MySQL', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value
    });
  };

  const handleOpenModal = (proveedor = null) => {
    if (proveedor) {
      setEditingId(proveedor.id);
      setFormData({
        nombre: proveedor.nombre || '',
        rut: proveedor.rut || '',
        email: proveedor.email || '',
        telefono: proveedor.telefono || '',
        direccion: proveedor.direccion || '',
        categoria: proveedor.categoria || '',
        activo: proveedor.activo !== undefined ? proveedor.activo : true
      });
    } else {
      setEditingId(null);
      setFormData({
        nombre: '',
        rut: '',
        email: '',
        telefono: '',
        direccion: '',
        categoria: '',
        activo: true
      });
    }
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await updateProveedor(editingId, formData);
        showToast('Proveedor actualizado exitosamente', 'success');
      } else {
        await createProveedor(formData);
        showToast('Proveedor guardado en MySQL exitosamente', 'success');
      }
      setShowModal(false);
      fetchProveedores();
    } catch (err) {
      showToast('Error al procesar la solicitud', 'error');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('¿Desea desactivar este proveedor?')) {
      try {
        await desactivarProveedor(id);
        showToast('Proveedor desactivado', 'success');
        fetchProveedores();
      } catch (err) {
        showToast('Error al desactivar el proveedor', 'error');
      }
    }
  };

  const handleToggleEstado = async (id, currentEstado) => {
    try {
      await toggleEstadoProveedor(id, !currentEstado);
      showToast(`Estado actualizado a ${!currentEstado ? 'Activo' : 'Inactivo'}`, 'success');
      fetchProveedores();
    } catch (err) {
      showToast('Error al actualizar estado', 'error');
    }
  };

  if (loading) return <Spinner />;

  return (
    <div className="page">

      <Toast
        message={toast.messages || []}
        messages={toast.messages || []}
        type={toast.type}
        onClose={() => setToast({ ...toast, show: false })}
      />

      {/* Barra superior */}
      <div className="page-toolbar">

        <input
          className="input"
          style={{ maxWidth: 320 }}
          type="text"
          placeholder="🔍 Buscar por nombre, RUT o categoría..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />

        <button
          className="btn btn-primary"
          onClick={() => handleOpenModal()}
        >
          + Nuevo Proveedor
        </button>

      </div>


      {/* Tabla de proveedores */}
      <div className="card">

        <div className="card-header">

          <span className="card-title">
            🏢 Proveedores
          </span>

          <span className="badge badge-default">
            {filteredProveedores.length} registros
          </span>

        </div>


        <div style={{ overflowX: 'auto' }}>

          {filteredProveedores.length === 0 ? (

            <div className="empty-state">

              <div className="icon">
                🏢
              </div>

              <p>
                No hay proveedores registrados.
              </p>

            </div>

          ) : (

            <table>

              <thead>
                <tr>
                  <th>#</th>
                  <th>Nombre</th>
                  <th>RUT</th>
                  <th>Contacto</th>
                  <th>Dirección</th>
                  <th>Categoría</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>


              <tbody>

                {filteredProveedores.map((p) => (

                  <tr key={p.id}>

                    <td>
                      <strong>#{p.id}</strong>
                    </td>


                    <td>
                      <strong>{p.nombre}</strong>
                    </td>


                    <td>
                      {p.rut}
                    </td>


                    <td>

                      <div>
                        {p.email || 'Sin correo'}
                      </div>

                      <div className="table-secondary">
                        {p.telefono || 'Sin teléfono'}
                      </div>

                    </td>


                    <td>
                      {p.direccion || 'Sin dirección'}
                    </td>


                    <td>

                      <span className="badge badge-info">
                        {p.categoria || 'N/A'}
                      </span>

                    </td>


                    <td>

                      <span
                        className={`badge ${
                          p.activo
                            ? 'badge-success'
                            : 'badge-danger'
                        }`}
                      >
                        {p.activo ? 'Activo' : 'Inactivo'}
                      </span>

                    </td>


                    <td>

                      <div className="action-btns">

                        <button
                          className="btn btn-secondary btn-sm"
                          onClick={() => handleOpenModal(p)}
                        >
                          ✏️ Editar
                        </button>


                        <button
                          className={`btn btn-sm ${
                            p.activo
                              ? 'btn-warning'
                              : 'btn-primary'
                          }`}
                          onClick={() =>
                            handleToggleEstado(
                              p.id,
                              p.activo
                            )
                          }
                        >
                          {p.activo
                            ? 'Desactivar'
                            : 'Activar'}
                        </button>


                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() =>
                            handleDelete(p.id)
                          }
                        >
                          🗑️
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


      {/* Modal */}
      {showModal && (

        <div
          className="modal-overlay"
          onClick={() => setShowModal(false)}
        >

          <div
            className="modal modal-large"
            onClick={(e) => e.stopPropagation()}
          >

            <h3>
              {editingId
                ? '✏️ Editar Proveedor'
                : '➕ Nuevo Proveedor'}
            </h3>


            <form onSubmit={handleSubmit}>

              {/* Nombre */}
              <div className="form-group">

                <label>
                  Nombre Empresa / Proveedor *
                </label>

                <input
                  className="input"
                  type="text"
                  name="nombre"
                  value={formData.nombre}
                  onChange={handleInputChange}
                  required
                  placeholder="Ej: Proveedor Logístico SPA"
                />

              </div>


              {/* RUT y categoría */}
              <div className="form-row">

                <div className="form-group">

                  <label>
                    RUT *
                  </label>

                  <input
                    className="input"
                    type="text"
                    name="rut"
                    value={formData.rut}
                    onChange={handleInputChange}
                    required
                    placeholder="Ej: 76.123.456-7"
                  />

                </div>


                <div className="form-group">

                  <label>
                    Categoría
                  </label>

                  <input
                    className="input"
                    type="text"
                    name="categoria"
                    value={formData.categoria}
                    onChange={handleInputChange}
                    placeholder="Ej: Insumos"
                  />

                </div>

              </div>


              {/* Email y teléfono */}
              <div className="form-row">

                <div className="form-group">

                  <label>
                    Email
                  </label>

                  <input
                    className="input"
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    placeholder="correo@empresa.cl"
                  />

                </div>


                <div className="form-group">

                  <label>
                    Teléfono
                  </label>

                  <input
                    className="input"
                    type="text"
                    name="telefono"
                    value={formData.telefono}
                    onChange={handleInputChange}
                    placeholder="+56 9 1234 5678"
                  />

                </div>

              </div>


              {/* Dirección */}
              <div className="form-group">

                <label>
                  Dirección
                </label>

                <input
                  className="input"
                  type="text"
                  name="direccion"
                  value={formData.direccion}
                  onChange={handleInputChange}
                  placeholder="Dirección del proveedor"
                />

              </div>


              {/* Estado */}
              <div className="form-group">

                <label
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '8px',
                    cursor: 'pointer'
                  }}
                >

                  <input
                    type="checkbox"
                    name="activo"
                    checked={formData.activo}
                    onChange={handleInputChange}
                  />

                  Proveedor activo

                </label>

              </div>


              {/* Botones */}
              <div className="modal-actions">

                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowModal(false)}
                >
                  Cancelar
                </button>


                <button
                  type="submit"
                  className="btn btn-primary"
                >
                  {editingId
                    ? 'Guardar Cambios'
                    : 'Crear Proveedor'}
                </button>

              </div>

            </form>

          </div>

        </div>

      )}

    </div>
  );
};

export default Proveedores;
