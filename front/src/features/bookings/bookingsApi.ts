import { httpClient } from '../../shared/api/httpClient';
import type { Booking, BookingRequest } from '../../shared/types/booking';

export async function getBookings(): Promise<Booking[]> {
  return httpClient.get<Booking[]>('/api/bookings');
}

export async function getBookingById(id: number): Promise<Booking> {
  return httpClient.get<Booking>(`/api/bookings/${id}`);
}

export async function createBooking(request: BookingRequest): Promise<Booking> {
  return httpClient.post<Booking>('/api/bookings', request);
}

export async function cancelBooking(id: number): Promise<Booking> {
  return httpClient.post<Booking>(`/api/bookings/${id}/cancel`, undefined);
}
