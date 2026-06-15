import { renderHook, waitFor } from '@testing-library/react';
import { useDashboard } from './useDashboard';
import * as api from '../services/api';

jest.mock('../services/api');

describe('useDashboard – Pruebas Unitarias', () => {

  beforeEach(() => { jest.clearAllMocks(); });

  test('retorna loading=true al inicio', () => {
    api.getDashboard.mockResolvedValue({ resumen: {}, productos: [], pedidos: [], envios: [] });
    const { result } = renderHook(() => useDashboard());
    expect(result.current.loading).toBe(true);
  });

  test('carga los datos correctamente', async () => {
    const mockData = {
      resumen: { totalProductos: 5, totalPedidos: 10 },
      productos: [], pedidos: [], envios: []
    };
    api.getDashboard.mockResolvedValue(mockData);
    const { result } = renderHook(() => useDashboard());
    await waitFor(() => expect(result.current.loading).toBe(false));
    expect(result.current.data).toEqual(mockData);
    expect(result.current.error).toBeNull();
  });

  test('captura el error si la API falla', async () => {
    api.getDashboard.mockRejectedValue(new Error('Servicio no disponible'));
    const { result } = renderHook(() => useDashboard());
    await waitFor(() => expect(result.current.loading).toBe(false));
    expect(result.current.error).toBe('Servicio no disponible');
    expect(result.current.data).toBeNull();
  });
});
