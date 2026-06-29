import { renderHook, waitFor } from '@testing-library/react';
import { useRockets } from './useRockets';
import { getRockets } from './rocketsApi';
import type { Rocket } from '../../shared/types/rocket';

vi.mock('./rocketsApi', () => ({ getRockets: vi.fn() }));

const falcon: Rocket = {
  id: 1,
  name: 'Falcon 9',
  capacity: 9,
  range: 'EARTH',
  status: 'ACTIVE',
  lastMaintenanceDate: '2026-01-15',
  nextMaintenanceDate: '2026-07-15',
};

beforeEach(() => {
  vi.mocked(getRockets).mockReset();
});

test('exposes data when the request resolves', async () => {
  vi.mocked(getRockets).mockResolvedValue([falcon]);

  const { result } = renderHook(() => useRockets());

  expect(result.current.isLoading).toBe(true);
  await waitFor(() => expect(result.current.isLoading).toBe(false));
  expect(result.current.data).toEqual([falcon]);
  expect(result.current.error).toBeNull();
});

test('exposes an error when the request rejects', async () => {
  vi.mocked(getRockets).mockRejectedValue(new Error('network down'));

  const { result } = renderHook(() => useRockets());

  await waitFor(() => expect(result.current.isLoading).toBe(false));
  expect(result.current.data).toBeNull();
  expect(result.current.error).toBeInstanceOf(Error);
});

test('exposes empty array when fleet is empty', async () => {
  vi.mocked(getRockets).mockResolvedValue([]);

  const { result } = renderHook(() => useRockets());

  await waitFor(() => expect(result.current.isLoading).toBe(false));
  expect(result.current.data).toEqual([]);
  expect(result.current.error).toBeNull();
});

test('refresh triggers a new fetch', async () => {
  vi.mocked(getRockets).mockResolvedValue([falcon]);

  const { result } = renderHook(() => useRockets());
  await waitFor(() => expect(result.current.isLoading).toBe(false));

  vi.mocked(getRockets).mockResolvedValue([falcon, { ...falcon, id: 2, name: 'Starship' }]);
  result.current.refresh();

  await waitFor(() => expect(result.current.data).toHaveLength(2));
  expect(getRockets).toHaveBeenCalledTimes(2);
});
