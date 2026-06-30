import { type Page, type Locator } from '@playwright/test';

/**
 * Page Object for the bookings list and form rendered at the SPA root.
 * Exposes intent-revealing locators only; assertions live in the specs.
 */
export class BookingsPage {
  readonly addBookingButton: Locator;
  readonly launchSelect: Locator;
  readonly passengerNameInput: Locator;
  readonly passengerEmailInput: Locator;
  readonly passengerPhoneInput: Locator;
  readonly submitButton: Locator;

  constructor(private readonly page: Page) {
    this.addBookingButton = page.getByTestId('add-booking-btn');
    this.launchSelect = page.getByTestId('field-launch');
    this.passengerNameInput = page.getByTestId('field-passenger-name');
    this.passengerEmailInput = page.getByTestId('field-passenger-email');
    this.passengerPhoneInput = page.getByTestId('field-passenger-phone');
    this.submitButton = page.getByTestId('submit-btn');
  }

  async goto(options?: Parameters<Page['goto']>[1]): Promise<void> {
    await this.page.goto('/', options);
  }

  async bookLaunch(
    launchLabel: string,
    passenger: { name: string; email: string; phone: string },
  ): Promise<void> {
    await this.addBookingButton.click();
    await this.launchSelect.selectOption({ label: launchLabel });
    await this.passengerNameInput.fill(passenger.name);
    await this.passengerEmailInput.fill(passenger.email);
    await this.passengerPhoneInput.fill(passenger.phone);
    await this.submitButton.click();
  }

  rowByPassenger(passengerName: string): Locator {
    return this.page.locator('[data-testid^="booking-row-"]').filter({ hasText: passengerName });
  }

  statusOf(row: Locator): Locator {
    return row.locator('.booking-status');
  }

  cancelButtonOf(row: Locator): Locator {
    return row.locator('[data-testid^="cancel-btn-"]');
  }
}
