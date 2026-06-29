import { httpClient } from '../../shared/api/httpClient';
import type { Rocket, RocketRequest } from '../../shared/types/rocket';

export async function getRockets(): Promise<Rocket[]> {
  return httpClient.get<Rocket[]>('/api/rockets');
}

export async function getRocketById(id: number): Promise<Rocket> {
  return httpClient.get<Rocket>(`/api/rockets/${id}`);
}

export async function createRocket(request: RocketRequest): Promise<Rocket> {
  return httpClient.post<Rocket>('/api/rockets', request);
}

export async function updateRocket(id: number, request: RocketRequest): Promise<Rocket> {
  return httpClient.put<Rocket>(`/api/rockets/${id}`, request);
}

export async function deleteRocket(id: number): Promise<void> {
  return httpClient.del(`/api/rockets/${id}`);
}
