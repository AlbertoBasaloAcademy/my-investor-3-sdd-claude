import { getRockets, getRocketById, createRocket, updateRocket, deleteRocket } from './rocketsApi';
import { httpClient } from '../../shared/api/httpClient';
import type { Rocket } from '../../shared/types/rocket';

vi.mock('../../shared/api/httpClient', () => ({
  httpClient: { get: vi.fn(), post: vi.fn(), put: vi.fn(), del: vi.fn() },
}));

const falcon: Rocket = {
  id: 1,
  name: 'Falcon 9',
  capacity: 9,
  rangeKm: 200_000,
  status: 'ACTIVE',
  lastMaintenanceDate: '2026-01-15',
  nextMaintenanceDate: '2026-07-15',
};

beforeEach(() => {
  vi.mocked(httpClient.get).mockReset();
  vi.mocked(httpClient.post).mockReset();
  vi.mocked(httpClient.put).mockReset();
  vi.mocked(httpClient.del).mockReset();
});

test('getRockets calls GET /api/rockets and returns rocket list', async () => {
  vi.mocked(httpClient.get).mockResolvedValue([falcon]);

  const result = await getRockets();

  expect(httpClient.get).toHaveBeenCalledWith('/api/rockets');
  expect(result).toEqual([falcon]);
});

test('getRocketById calls GET /api/rockets/:id', async () => {
  vi.mocked(httpClient.get).mockResolvedValue(falcon);

  const result = await getRocketById(1);

  expect(httpClient.get).toHaveBeenCalledWith('/api/rockets/1');
  expect(result).toEqual(falcon);
});

test('createRocket calls POST /api/rockets with payload', async () => {
  const { id: _id, ...request } = falcon;
  vi.mocked(httpClient.post).mockResolvedValue(falcon);

  const result = await createRocket(request);

  expect(httpClient.post).toHaveBeenCalledWith('/api/rockets', request);
  expect(result).toEqual(falcon);
});

test('updateRocket calls PUT /api/rockets/:id with payload', async () => {
  const { id: _id, ...request } = falcon;
  vi.mocked(httpClient.put).mockResolvedValue(falcon);

  const result = await updateRocket(1, request);

  expect(httpClient.put).toHaveBeenCalledWith('/api/rockets/1', request);
  expect(result).toEqual(falcon);
});

test('deleteRocket calls DELETE /api/rockets/:id', async () => {
  vi.mocked(httpClient.del).mockResolvedValue(undefined);

  await deleteRocket(1);

  expect(httpClient.del).toHaveBeenCalledWith('/api/rockets/1');
});

test('getRockets propagates client errors', async () => {
  vi.mocked(httpClient.get).mockRejectedValue(new Error('network error'));

  await expect(getRockets()).rejects.toThrow('network error');
});
