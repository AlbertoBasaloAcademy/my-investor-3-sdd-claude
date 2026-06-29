export type RocketStatus = 'ACTIVE' | 'MAINTENANCE' | 'RETIRED';

export type Rocket = {
  id: number;
  name: string;
  capacity: number;
  rangeKm: number;
  status: RocketStatus;
  lastMaintenanceDate: string | null;
  nextMaintenanceDate: string | null;
};

export type RocketRequest = Omit<Rocket, 'id'>;
