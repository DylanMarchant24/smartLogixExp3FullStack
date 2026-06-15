import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import Inventario from './pages/Inventario';
import Pedidos from './pages/Pedidos';
import Envios from './pages/Envios';

/**
 * App – Raíz de la aplicación.
 * Usa React Router v6 para el ruteo SPA.
 * El Layout envuelve todas las páginas (Navbar + Sidebar persistentes).
 */
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard"  element={<Dashboard />} />
          <Route path="inventario" element={<Inventario />} />
          <Route path="pedidos"    element={<Pedidos />} />
          <Route path="envios"     element={<Envios />} />
          <Route path="*"          element={<Navigate to="/dashboard" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
