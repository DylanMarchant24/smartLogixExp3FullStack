import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import StatCard from './StatCard';

describe('StatCard – Pruebas Unitarias', () => {

  test('renderiza el título correctamente', () => {
    render(<StatCard title="Total Pedidos" value={42} icon="🛒" color="blue" />);
    expect(screen.getByText('Total Pedidos')).toBeInTheDocument();
  });

  test('renderiza el valor correctamente', () => {
    render(<StatCard title="Inventario" value={100} icon="📦" color="green" />);
    expect(screen.getByText('100')).toBeInTheDocument();
  });

  test('muestra "–" cuando value es undefined', () => {
    render(<StatCard title="Sin datos" icon="⚠️" color="red" />);
    expect(screen.getByText('–')).toBeInTheDocument();
  });

  test('renderiza el subtexto cuando se provee la prop "sub"', () => {
    render(<StatCard title="Envíos" value={5} icon="🚚" color="orange" sub="Despachos activos" />);
    expect(screen.getByText('Despachos activos')).toBeInTheDocument();
  });

  test('NO renderiza subtexto cuando la prop "sub" no se provee', () => {
    render(<StatCard title="Sin sub" value={0} icon="📊" color="purple" />);
    expect(screen.queryByText(/despachos/i)).not.toBeInTheDocument();
  });

  test('aplica el role region y aria-label para accesibilidad', () => {
    render(<StatCard title="Accesibilidad" value={1} icon="✅" color="green" />);
    expect(screen.getByRole('region', { name: 'Accesibilidad' })).toBeInTheDocument();
  });

  test('renderiza el ícono correctamente', () => {
    render(<StatCard title="Con ícono" value={7} icon="🧪" color="blue" />);
    expect(screen.getByText('🧪')).toBeInTheDocument();
  });
});
