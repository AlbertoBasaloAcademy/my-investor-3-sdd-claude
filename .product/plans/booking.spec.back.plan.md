---
plan-type: spec
tier: back
---
# spec - booking - back

## Specification

Replace the payment-oriented `Booking` status (`CONFIRMED`, `CANCELLED`, `PAYED`) with a simple reservation lifecycle (`CREATED`, `CANCELLED`), add a required `passengerPhone` field, and replace the generic update/delete endpoints with a single cancel action.

**Context**: [booking.spec.md](../specs/booking.spec.md)

### Data model

- `BookingStatus` enum shrinks to `CREATED`, `CANCELLED` (drop `CONFIRMED`, `PAYED`).
- `Booking` entity gains `passengerPhone` (`String`, column `passenger_phone`, not null). Status is no longer client-supplied on create — it always starts `CREATED`.
- No migration script needed: `ddl-auto: update` (see `back/src/main/resources/application.yml`) regenerates the `booking` table column from the entity change.
- Lifecycle is one-directional: `CREATED` → `CANCELLED` only, via a dedicated cancel action — no generic update, no delete.

## Implementation Steps

### Step 1: Narrow the booking status lifecycle
Replace the three payment-oriented values with the two lifecycle values the spec defines.
- Paths:
    - back/src/main/java/dev/aiddbot/abjavareact/booking/BookingStatus.java
- [ ] Replace `CONFIRMED, CANCELLED, PAYED` with `CREATED, CANCELLED`.

### Step 2: Add passenger phone to the Booking entity
Capture the new required contact field alongside name and email.
- Paths:
    - back/src/main/java/dev/aiddbot/abjavareact/booking/Booking.java
- [ ] Add `passengerPhone` field (`@Column(name = "passenger_phone", nullable = false)`) with getter/setter.
- [ ] Update the constructor to accept `passengerPhone` in place of the client-supplied `status` (status is always set to `CREATED` internally, not passed in).

### Step 3: Update request/response DTOs
Mirror the entity change in the API contracts.
- Paths:
    - back/src/main/java/dev/aiddbot/abjavareact/booking/BookingRequest.java
    - back/src/main/java/dev/aiddbot/abjavareact/booking/BookingResponse.java
- [ ] `BookingRequest`: drop `status`, add `passengerPhone` → `(Long launchId, String passengerName, String passengerEmail, String passengerPhone)`.
- [ ] `BookingResponse`: add `passengerPhone` after `passengerEmail`.

### Step 4: Rework BookingService for the create/cancel lifecycle
Replace the generic CRUD methods with create + cancel only.
- Paths:
    - back/src/main/java/dev/aiddbot/abjavareact/booking/BookingService.java
- [ ] `create()`: validate `launchId`, `passengerName`, `passengerEmail`, `passengerPhone` are all present/non-blank (`400` via `ResponseStatusException`, same pattern as the existing `validate()`); always construct the `Booking` with status `CREATED`.
- [ ] Remove `update(Long, BookingRequest)` and `delete(Long)`.
- [ ] Add `cancel(Long id)`: load the booking (`404 ResponseStatusException` "Booking not found: {id}" if missing, same pattern as `findById`), set status to `CANCELLED`, save, return the mapped `BookingResponse`.
- [ ] Update `toResponse()` to include `passengerPhone`.

### Step 5: Replace update/delete endpoints with a cancel endpoint
Expose the new lifecycle action over REST.
- Paths:
    - back/src/main/java/dev/aiddbot/abjavareact/booking/BookingController.java
- [ ] Remove `@PutMapping("/{id}") update(...)` and `@DeleteMapping("/{id}") delete(...)`.
- [ ] Add `@PostMapping("/{id}/cancel") cancel(@PathVariable Long id)` returning the updated `BookingResponse` (`200 OK`).

### Step 6: Update backend tests for the new lifecycle
Keep coverage aligned with the new contract.
- Paths:
    - back/src/test/java/dev/aiddbot/abjavareact/booking/BookingRepositoryTest.java
    - back/src/test/java/dev/aiddbot/abjavareact/booking/BookingServiceTest.java
    - back/src/test/java/dev/aiddbot/abjavareact/booking/BookingControllerTest.java
- [ ] `BookingRepositoryTest`: persist/retrieve bookings with `passengerPhone` and only `CREATED`/`CANCELLED` statuses.
- [ ] `BookingServiceTest`: replace `CONFIRMED`/`PAYED` fixtures with `CREATED`; add cases for missing-phone `400`, successful `cancel()`, and `cancel()` on an unknown id throwing `404`; remove update/delete test cases.
- [ ] `BookingControllerTest`: replace `PUT`/`DELETE` assertions with `POST /api/bookings/{id}/cancel` assertions (`200` + status `CANCELLED` on success, `404` for an unknown id); keep create/list/`400`-on-missing-field coverage, extended to cover the phone field.
