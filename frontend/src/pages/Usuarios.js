import React, { useCallback, useEffect, useState } from 'react';
import { getUsuarios } from '../services/api';
import Spinner from '../components/Spinner';
import Toast from '../components/Toast';
import { useToast } from '../hooks/useToast';
import './PageStyles.css';

export default function Usuarios() {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const { toasts, error } = useToast();
  const load = useCallback(async () => { setLoading(true); try { setUsuarios(await getUsuarios()); } catch (e) { error(e.message); } finally { setLoading(false); } }, [error]);
  useEffect(() => { load(); }, [load]);
  const filtered = usuarios.filter(u => `${u.nombre} ${u.email} ${u.rol}`.toLowerCase().includes(search.toLowerCase()));
  if (loading) return <Spinner message="Cargando usuarios…" />;
  return <div className="page"><Toast toasts={toasts} /><div className="page-toolbar"><input className="input" style={{maxWidth:320}} placeholder="🔍 Buscar usuario…" value={search} onChange={e=>setSearch(e.target.value)} /><button className="btn btn-secondary" onClick={load}>↻ Actualizar</button></div><div className="card"><div className="card-header"><span className="card-title">👥 Usuarios registrados</span><span className="badge badge-default">{filtered.length} registros</span></div><div style={{overflowX:'auto'}}><table><thead><tr><th>#</th><th>Nombre</th><th>Correo</th><th>Rol</th><th>Estado</th><th>Creación</th><th>Último login</th></tr></thead><tbody>{filtered.map(u=><tr key={u.id}><td>#{u.id}</td><td><strong>{u.nombre}</strong></td><td>{u.email}</td><td><span className="badge badge-default">{u.rol}</span></td><td><span className={`badge ${u.activo ? 'badge-success' : 'badge-danger'}`}>{u.activo ? 'Activo' : 'Inactivo'}</span></td><td>{u.fechaCreacion ? new Date(u.fechaCreacion).toLocaleString('es-CL') : '—'}</td><td>{u.ultimoLogin ? new Date(u.ultimoLogin).toLocaleString('es-CL') : 'Nunca'}</td></tr>)}</tbody></table>{filtered.length===0 && <div className="empty-state"><div className="icon">👥</div><p>No hay usuarios registrados.</p></div>}</div></div></div>;
}
