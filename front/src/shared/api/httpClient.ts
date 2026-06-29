const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

async function request(method: string, path: string, body?: unknown): Promise<Response> {
  const headers: Record<string, string> = { Accept: 'application/json' };
  if (body !== undefined) headers['Content-Type'] = 'application/json';

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {
    throw new Error(`Request to ${path} failed with status ${response.status}`);
  }

  return response;
}

async function get<T>(path: string): Promise<T> {
  return (await request('GET', path)).json() as T;
}

async function post<T>(path: string, body: unknown): Promise<T> {
  return (await request('POST', path, body)).json() as T;
}

async function put<T>(path: string, body: unknown): Promise<T> {
  return (await request('PUT', path, body)).json() as T;
}

async function del(path: string): Promise<void> {
  await request('DELETE', path);
}

export const httpClient = { get, post, put, del };
