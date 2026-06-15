import { useState, useEffect, useCallback } from 'react';
import { getDashboard } from '../services/api';

/**
 * PATRÓN: Custom Hook
 * Encapsula la lógica de obtención y refresco del dashboard.
 * El componente Dashboard solo se preocupa del renderizado.
 */
export function useDashboard() {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(true);
  const [error,   setError]   = useState(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await getDashboard();
      setData(result);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { data, loading, error, refresh: fetchData };
}
