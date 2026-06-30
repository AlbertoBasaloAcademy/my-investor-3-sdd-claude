export type BookingStatus = 'CREATED' | 'CANCELLED';

export type Booking = {
  id: number;
  launchId: number;
  launchRocketName: string;
  launchDate: string;
  passengerName: string;
  passengerEmail: string;
  passengerPhone: string;
  status: BookingStatus;
};

export type BookingRequest = Omit<Booking, 'id' | 'launchRocketName' | 'launchDate' | 'status'>;
