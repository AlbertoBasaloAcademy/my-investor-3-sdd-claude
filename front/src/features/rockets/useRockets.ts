import { useEffect, useState } from 'react';
import { getRockets } from './rocketsApi';
import type { Rocket } from '../../shared/types/rocket';

export function useRockets() {
  const [data, setData] = useState<Rocket[] | null>(null);
  const [error, setError] = useState<Error | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    let active = true;
    setIsLoading(true);
    setError(null);

    getRockets()
      .then((rockets) => {
        if (active) setData(rockets);
      })
      .catch((cause: unknown) => {
        if (active)
          setError(cause instanceof Error ? cause : new Error(String(cause)));
      })
      .finally(() => {
        if (active) setIsLoading(false);
      });

    return () => {
      active = false;
    };
  }, [refreshKey]);

  const refresh = () => setRefreshKey((k) => k + 1);

  return { data, error, isLoading, refresh } as const;
}
