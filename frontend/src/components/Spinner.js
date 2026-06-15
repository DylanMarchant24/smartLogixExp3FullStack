import React from 'react';

function Spinner({ message = 'Cargando…' }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column',
                  alignItems: 'center', justifyContent: 'center',
                  padding: '64px', gap: '16px' }}>
      <div className="spinner" />
      <p style={{ color: 'var(--text-muted)', fontSize: '.875rem' }}>{message}</p>
    </div>
  );
}

export default Spinner;
