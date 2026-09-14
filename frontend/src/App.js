import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import Dashboard from './pages/Dashboard';
import Inventario from './pages/Inventario';
import Pedidos from './pages/Pedidos';
import Envios from './pages/Envios';
import Login from './pages/Login';
import Pagos from './pages/Pagos';
import Sucursales from './pages/Sucursales';
import Usuarios from './pages/Usuarios';
import Proveedores from './pages/Proveedores';
import Calificaciones from './pages/Calificaciones';
import Cupones from './pages/Cupones';
import Notificaciones from './pages/Notificaciones';

/**
 * App – Raíz de la aplicación.
 * Usa React Router v6 para el ruteo SPA.
 * El Layout envuelve todas las páginas (Navbar + Sidebar persistentes).
 */
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />

        <Route
          path="/"
          element={
            <ProtectedRoute>
              <Layout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="inventario" element={<Inventario />} />
          <Route path="pedidos" element={<Pedidos />} />
          <Route path="envios" element={<Envios />} />
          <Route path="pagos" element={<Pagos />} />
          <Route path="sucursales" element={<Sucursales />} />
          <Route path="usuarios" element={<Usuarios />} />
          <Route path="proveedores" element={<Proveedores />} />
          <Route path="calificaciones" element={<Calificaciones />} />
          <Route path="cupones" element={<Cupones />} />
          <Route path="notificaciones" element={<Notificaciones />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
