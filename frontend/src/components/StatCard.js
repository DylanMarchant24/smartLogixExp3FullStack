import React from 'react';
import './StatCard.css';

/**
 * StatCard – Componente reutilizable para métricas del Dashboard.
 * PATRÓN: Component (React) – encapsula presentación y no tiene lógica de negocio.
 *
 * Props:
 *   title   {string}  – etiqueta de la métrica
 *   value   {number}  – valor principal
 *   icon    {string}  – emoji o texto para el ícono
 *   color   {string}  – variante de color: 'blue' | 'green' | 'orange' | 'purple' | 'red'
 *   sub     {string}  – texto secundario opcional
 */
function StatCard({ title, value, icon, color = 'blue', sub }) {
  return (
    <div className={`stat-card stat-card--${color}`} role="region" aria-label={title}>
      <div className="stat-card__icon-wrap">
        <span className="stat-card__icon">{icon}</span>
      </div>
      <div className="stat-card__body">
        <p className="stat-card__title">{title}</p>
        <p className="stat-card__value">{value ?? '–'}</p>
        {sub && <p className="stat-card__sub">{sub}</p>}
      </div>
    </div>
  );
}

export default StatCard;
