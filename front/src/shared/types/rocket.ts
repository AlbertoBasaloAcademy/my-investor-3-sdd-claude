export type RocketStatus = 'ACTIVE' | 'MAINTENANCE' | 'RETIRED';
export type RocketRange = 'EARTH' | 'MOON' | 'MARS';

export type Rocket = {
  id: number;
  name: string;
  capacity: number;
  range: RocketRange;
  status: RocketStatus;
  lastMaintenanceDate: string | null;
  nextMaintenanceDate: string | null;
};

export type RocketRequest = Omit<Rocket, 'id'>;
