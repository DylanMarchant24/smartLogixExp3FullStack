import React, { useCallback, useEffect, useState } from 'react';
import {
  getPagos,
  getPagosPorPedido,
  procesarPago
} from '../services/api';

import Spinner from '../components/Spinner';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';
import './PageStyles.css';

function Pagos() {
  const [pagos, setPagos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [pedidoBusqueda, setPedidoBusqueda] = useState('');

  const [form, setForm] = useState({
    pedidoId: '',
    monto: '',
    tarjetaToken: 'TEST-APROBADA',
    ultimos4: ''
  });

  const { toasts, success, error } = useToast();

  const cargarPagos = useCallback(async () => {
    setLoading(true);

    try {
      const data = await getPagos();
      setPagos(Array.isArray(data) ? data : []);
    } catch (e) {
      error(e.message);
    } finally {
      setLoading(false);
    }
  }, [error]);

  useEffect(() => {
    cargarPagos();
  }, [cargarPagos]);

  function cambiarCampo(campo) {
    return (e) => {
      setForm((prev) => ({
        ...prev,
        [campo]: e.target.value
      }));
    };
  }

  async function buscarPorPedido() {
    if (!pedidoBusqueda) {
      cargarPagos();
      return;
    }

    setLoading(true);

    try {
      const data = await getPagosPorPedido(pedidoBusqueda);

      setPagos(
        Array.isArray(data)
          ? data
          : data
            ? [data]
            : []
      );
    } catch (e) {
      error(e.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setSaving(true);

    try {
      const resultado = await procesarPago({
        pedidoId: Number(form.pedidoId),
        monto: Number(form.monto),
        tarjetaToken: form.tarjetaToken,
        ultimos4: form.ultimos4
      });

      if (resultado.estado === 'APROBADO') {
        success('Pago aprobado correctamente');
      } else {
        error(resultado.motivo || 'Pago rechazado');
      }

      setModal(false);

      setForm({
        pedidoId: '',
        monto: '',
        tarjetaToken: 'TEST-APROBADA',
        ultimos4: ''
      });

      await cargarPagos();

    } catch (e) {
      error(e.message);
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return <Spinner message="Cargando pagos…" />;
  }

  return (
    <div className="page">
      <Toast toasts={toasts} />

      <div className="page-toolbar">
        <div style={{ display: 'flex', gap: 8 }}>
          <input
            className="input"
            placeholder="Buscar por ID de pedido"
            value={pedidoBusqueda}
            onChange={(e) => setPedidoBusqueda(e.target.value)}
          />

          <button
            className="btn btn-secondary"
            onClick={buscarPorPedido}
          >
            Buscar
          </button>

          <button
            className="btn btn-secondary"
            onClick={() => {
              setPedidoBusqueda('');
              cargarPagos();
            }}
          >
            Limpiar
          </button>
        </div>

        <button
          className="btn btn-primary"
          onClick={() => setModal(true)}
        >
          💳 Procesar Pago
        </button>
      </div>

      <div className="card">
        <div className="card-header">
          <span className="card-title">
            💳 Historial de Pagos
          </span>

          <span className="badge badge-default">
            {pagos.length} registros
          </span>
        </div>

        {pagos.length === 0 ? (
          <div className="empty-state">
            <div className="icon">💳</div>
            <p>No hay pagos registrados.</p>
          </div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Pedido</th>
                  <th>Monto</th>
                  <th>Estado</th>
                  <th>Últimos 4</th>
                  <th>Transacción</th>
                </tr>
              </thead>

              <tbody>
                {pagos.map((pago) => (
                  <tr key={pago.id}>
                    <td>#{pago.id}</td>
                    <td>#{pago.pedidoId}</td>
                    <td>
                      ${Number(pago.monto || 0).toLocaleString('es-CL')}
                    </td>
                    <td>
                      <span
                        className={`badge ${
                          pago.estado === 'APROBADO'
                            ? 'badge-success'
                            : 'badge-danger'
                        }`}
                      >
                        {pago.estado}
                      </span>
                    </td>
                    <td>{pago.ultimos4 || '—'}</td>
                    <td>{pago.codigoTransaccion || '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {modal && (
        <div
          className="modal-overlay"
          onClick={() => setModal(false)}
        >
          <div
            className="modal"
            onClick={(e) => e.stopPropagation()}
          >
            <h3>💳 Procesar Pago</h3>

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>ID del pedido</label>
                <input
                  className="input"
                  type="number"
                  required
                  value={form.pedidoId}
                  onChange={cambiarCampo('pedidoId')}
                />
              </div>

              <div className="form-group">
                <label>Monto</label>
                <input
                  className="input"
                  type="number"
                  min="1"
                  required
                  value={form.monto}
                  onChange={cambiarCampo('monto')}
                />
              </div>

              <div className="form-group">
                <label>Tarjeta simulada</label>
                <select
                  className="input"
                  value={form.tarjetaToken}
                  onChange={cambiarCampo('tarjetaToken')}
                >
                  <option value="TEST-APROBADA">
                    Tarjeta aprobada
                  </option>

                  <option value="TEST-RECHAZADA">
                    Tarjeta rechazada
                  </option>
                </select>
              </div>

              <div className="form-group">
                <label>Últimos 4 dígitos</label>
                <input
                  className="input"
                  maxLength="4"
                  pattern="[0-9]{4}"
                  required
                  value={form.ultimos4}
                  onChange={cambiarCampo('ultimos4')}
                  placeholder="4242"
                />
              </div>

              <div className="modal-actions">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setModal(false)}
                >
                  Cancelar
                </button>

                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={saving}
                >
                  {saving ? 'Procesando...' : 'Procesar Pago'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Pagos;