import { test, expect, request as playwrightRequest } from '@playwright/test';
import { BookingsPage } from '../pages/BookingsPage';

const API_URL = process.env.E2E_API_URL ?? 'http://localhost:8080';

async function seedLaunch(): Promise<string> {
  const api = await playwrightRequest.newContext({ baseURL: API_URL });
  const suffix = `${Date.now()}-${Math.floor(Math.random() * 10_000)}`;

  const rocket = await (
    await api.post('/api/rockets', {
      data: {
        name: `E2E Rocket ${suffix}`,
        capacity: 4,
        range: 'EARTH',
        status: 'ACTIVE',
        lastMaintenanceDate: '2026-01-01',
        nextMaintenanceDate: '2026-12-01',
      },
    })
  ).json();

  const launch = await (
    await api.post('/api/launches', {
      data: {
        rocketId: rocket.id,
        date: '2027-09-01',
        pricePerSeat: 100000,
        status: 'CREATED',
      },
    })
  ).json();

  await api.dispose();
  return `${launch.rocketName} — ${launch.date}`;
}

test.describe('Booking lifecycle', () => {
  let launchLabel: string;

  test.beforeAll(async () => {
    launchLabel = await seedLaunch();
  });

  test('books a launch and shows it with status CREATED', async ({ page }) => {
    const bookings = new BookingsPage(page);
    await bookings.goto();

    const passenger = {
      name: `E2E Passenger ${Date.now()}`,
      email: 'e2e.passenger@example.com',
      phone: '+1 555 0100',
    };
    await bookings.bookLaunch(launchLabel, passenger);

    const row = bookings.rowByPassenger(passenger.name);
    await expect(row).toBeVisible();
    await expect(bookings.statusOf(row)).toHaveText('CREATED');
  });

  test('cancels a CREATED booking', async ({ page }) => {
    const bookings = new BookingsPage(page);
    await bookings.goto();

    const passenger = {
      name: `E2E Cancel ${Date.now()}`,
      email: 'e2e.cancel@example.com',
      phone: '+1 555 0101',
    };
    await bookings.bookLaunch(launchLabel, passenger);

    const row = bookings.rowByPassenger(passenger.name);
    await expect(row).toBeVisible();

    await bookings.cancelButtonOf(row).click();

    await expect(bookings.statusOf(row)).toHaveText('CANCELLED');
    await expect(bookings.cancelButtonOf(row)).toHaveCount(0);
  });
});
