---
plan-type: spec
tier: front
---
# spec - booking - front

## Specification

Update the bookings feature to capture passenger phone, drop the payment-oriented status editing, and replace edit/delete actions with a single cancel action — matching the backend's new `CREATED`/`CANCELLED` lifecycle. Add Playwright coverage for the booking and cancel flows.

**Context**: [booking.spec.md](../specs/booking.spec.md)

### Data model

- `BookingStatus` narrows to `'CREATED' | 'CANCELLED'`.
- `Booking` gains `passengerPhone: string`.
- `BookingRequest` drops `status` (the server always creates as `CREATED`) and adds `passengerPhone`.

## Implementation Steps

### Step 1: Update shared booking types
Mirror the new backend DTO shape.
- Paths:
    - front/src/shared/types/booking.ts
- [ ] `BookingStatus` = `'CREATED' | 'CANCELLED'`.
- [ ] `Booking` adds `passengerPhone: string`.
- [ ] `BookingRequest` = `Omit<Booking, 'id' | 'launchRocketName' | 'launchDate' | 'status'>` (no status field — server sets it).

### Step 2: Replace update/delete API calls with a cancel call
Align the API client with the new backend endpoint.
- Paths:
    - front/src/features/bookings/bookingsApi.ts
- [ ] Remove `updateBooking` and `deleteBooking`.
- [ ] Add `cancelBooking(id: number): Promise<Booking>` calling `httpClient.post('/api/bookings/{id}/cancel')`.

### Step 3: Rework the booking form to capture phone and drop status editing
Capture the new required field and stop letting the client set status.
- Paths:
    - front/src/features/bookings/BookingList.tsx
- [ ] `FormState`: replace `status` with `passengerPhone`; `EMPTY_FORM` drops the `status` default and adds `passengerPhone: ''`.
- [ ] `formToRequest()`: build the `BookingRequest` without `status`, including `passengerPhone`.
- [ ] Remove the `Status` `<select>` field from the form.
- [ ] Add a `Passenger Phone` `<input required>` field (`data-testid="field-passenger-phone"`).
- [ ] Keep surfacing validation errors via the existing `formError` state (HTML `required` plus the server's `400` message caught in `handleSubmit`) so missing name/email/phone is visible to the user.

### Step 4: Replace edit/delete actions with a cancel-only action
Match the one-directional `CREATED` → `CANCELLED` lifecycle.
- Paths:
    - front/src/features/bookings/BookingList.tsx
- [ ] Remove `openEdit`, `editingId`, `bookingToForm`, and the `confirmDeleteId`/`handleDelete` delete-confirmation flow.
- [ ] Add `handleCancel(id)` calling `cancelBooking(id)` then `refresh()`.
- [ ] In the Actions column: when `status === 'CREATED'`, show a `Cancel` button (`data-testid="cancel-btn-{id}"`); when `status === 'CANCELLED'`, show only the status badge with no action.

### Step 5: Clean up booking styles
Drop styling for removed interactions, keep status badge styling for the two lifecycle states.
- Paths:
    - front/src/features/bookings/BookingList.css
- [ ] Remove the now-unused delete-confirmation styles (`.booking-confirm` and related).
- [ ] Keep/verify `.booking-status--created` and `.booking-status--cancelled` badge styles.

### Step 6: Update frontend unit tests
Keep API client coverage aligned with the new contract.
- Paths:
    - front/src/features/bookings/bookingsApi.test.ts
- [ ] Replace the `updateBooking`/`deleteBooking` test cases with a `cancelBooking` test asserting a `POST` to `/api/bookings/{id}/cancel`.

### Step 7: Add Playwright end-to-end coverage for the booking lifecycle
Cover the full booking and cancellation flow through the UI, per the spec's acceptance criteria.
- Paths:
    - e2e/pages/BookingsPage.ts
    - e2e/tests/booking.spec.ts
- [ ] Add a `BookingsPage` page object (mirroring `e2e/pages/HealthPage.ts` conventions) exposing selectors for the launch select, passenger name/email/phone fields, submit button, and per-row cancel button.
- [ ] Test: select a launch, fill passenger name/email/phone, submit, assert the new row shows status `CREATED`.
- [ ] Test: cancel a `CREATED` booking from the list, assert its status changes to `CANCELLED` and no cancel action is offered for it anymore.
