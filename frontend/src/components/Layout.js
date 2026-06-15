import React, { useState } from 'react';
import { Outlet, NavLink, useLocation } from 'react-router-dom';
import './Layout.css';

const NAV = [
  { to: '/dashboard',  icon: '📊', label: 'Dashboard'  },
  { to: '/inventario', icon: '📦', label: 'Inventario'  },
  { to: '/pedidos',    icon: '🛒', label: 'Pedidos'     },
  { to: '/envios',     icon: '🚚', label: 'Envíos'      },
];

function Layout() {
  const [collapsed, setCollapsed] = useState(false);
  const location = useLocation();
  const pageTitle = NAV.find((n) => location.pathname.startsWith(n.to))?.label || 'SmartLogix';

  return (
    <div className={`layout ${collapsed ? 'collapsed' : ''}`}>
      {/* ── Sidebar ── */}
      <aside className="sidebar">
        <div className="sidebar-brand">
          <span className="brand-icon">⚡</span>
          {!collapsed && <span className="brand-name">SmartLogix</span>}
        </div>

        <nav className="sidebar-nav">
          {NAV.map(({ to, icon, label }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
              title={collapsed ? label : ''}
            >
              <span className="nav-icon">{icon}</span>
              {!collapsed && <span className="nav-label">{label}</span>}
            </NavLink>
          ))}
        </nav>

        <button className="collapse-btn" onClick={() => setCollapsed((c) => !c)}>
          {collapsed ? '→' : '←'}
        </button>
      </aside>

      {/* ── Contenido principal ── */}
      <div className="main-wrapper">
        <header className="topbar">
          <div className="topbar-left">
            <h1 className="page-title">{pageTitle}</h1>
          </div>
          <div className="topbar-right">
            <div className="topbar-badge">
              <span className="status-dot" />
              Sistema activo
            </div>
            <div className="avatar">SL</div>
          </div>
        </header>

        <main className="main-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default Layout;
