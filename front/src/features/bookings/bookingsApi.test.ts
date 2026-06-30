import { getBookings, getBookingById, createBooking, cancelBooking } from './bookingsApi';
import { httpClient } from '../../shared/api/httpClient';
import type { Booking } from '../../shared/types/booking';

vi.mock('../../shared/api/httpClient', () => ({
  httpClient: { get: vi.fn(), post: vi.fn(), put: vi.fn(), del: vi.fn() },
}));

const booking: Booking = {
  id: 1,
  launchId: 2,
  launchRocketName: 'Falcon 9',
  launchDate: '2027-06-15',
  passengerName: 'Ada Lovelace',
  passengerEmail: 'ada@example.com',
  passengerPhone: '+1 555 0100',
  status: 'CREATED',
};

beforeEach(() => {
  vi.mocked(httpClient.get).mockReset();
  vi.mocked(httpClient.post).mockReset();
  vi.mocked(httpClient.put).mockReset();
  vi.mocked(httpClient.del).mockReset();
});

test('getBookings calls GET /api/bookings and returns booking list', async () => {
  vi.mocked(httpClient.get).mockResolvedValue([booking]);

  const result = await getBookings();

  expect(httpClient.get).toHaveBeenCalledWith('/api/bookings');
  expect(result).toEqual([booking]);
});

test('getBookingById calls GET /api/bookings/:id', async () => {
  vi.mocked(httpClient.get).mockResolvedValue(booking);

  const result = await getBookingById(1);

  expect(httpClient.get).toHaveBeenCalledWith('/api/bookings/1');
  expect(result).toEqual(booking);
});

test('createBooking calls POST /api/bookings with payload', async () => {
  const { id: _id, launchRocketName: _rn, launchDate: _ld, status: _status, ...request } = booking;
  vi.mocked(httpClient.post).mockResolvedValue(booking);

  const result = await createBooking(request);

  expect(httpClient.post).toHaveBeenCalledWith('/api/bookings', request);
  expect(result).toEqual(booking);
});

test('cancelBooking calls POST /api/bookings/:id/cancel', async () => {
  const cancelled = { ...booking, status: 'CANCELLED' as const };
  vi.mocked(httpClient.post).mockResolvedValue(cancelled);

  const result = await cancelBooking(1);

  expect(httpClient.post).toHaveBeenCalledWith('/api/bookings/1/cancel', undefined);
  expect(result).toEqual(cancelled);
});

test('getBookings propagates client errors', async () => {
  vi.mocked(httpClient.get).mockRejectedValue(new Error('network error'));

  await expect(getBookings()).rejects.toThrow('network error');
});
