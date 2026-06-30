import { useState, useEffect } from 'react';
import { useBookings } from './useBookings';
import { createBooking, cancelBooking } from './bookingsApi';
import { getLaunches } from '../launches/launchesApi';
import type { BookingRequest } from '../../shared/types/booking';
import type { Launch } from '../../shared/types/launch';
import './BookingList.css';

type FormState = {
  launchId: string;
  passengerName: string;
  passengerEmail: string;
  passengerPhone: string;
};

const EMPTY_FORM: FormState = {
  launchId: '',
  passengerName: '',
  passengerEmail: '',
  passengerPhone: '',
};

function formToRequest(form: FormState): BookingRequest {
  return {
    launchId: Number(form.launchId),
    passengerName: form.passengerName,
    passengerEmail: form.passengerEmail,
    passengerPhone: form.passengerPhone,
  };
}

export function BookingList() {
  const { data: bookings, error, isLoading, refresh } = useBookings();
  const [launches, setLaunches] = useState<Launch[]>([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [formError, setFormError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    getLaunches().then(setLaunches).catch(() => {});
  }, []);

  function openCreate() {
    setForm(EMPTY_FORM);
    setFormError(null);
    setShowForm(true);
  }

  function cancelForm() {
    setShowForm(false);
    setFormError(null);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setFormError(null);
    setIsSaving(true);
    try {
      await createBooking(formToRequest(form));
      setShowForm(false);
      refresh();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : 'Operation failed');
    } finally {
      setIsSaving(false);
    }
  }

  async function handleCancel(id: number) {
    try {
      await cancelBooking(id);
      refresh();
    } catch {
      // no-op
    }
  }

  function handleFieldChange(field: keyof FormState, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  if (isLoading) {
    return (
      <section className="booking-section" aria-busy="true">
        <p className="booking-loading" data-testid="bookings-loading">
          <span className="booking-spinner" aria-hidden="true" />
          Loading bookings…
        </p>
      </section>
    );
  }

  if (error) {
    return (
      <section className="booking-section">
        <p className="booking-error" data-testid="bookings-error" role="alert">
          Failed to load bookings — {error.message}
        </p>
      </section>
    );
  }

  return (
    <section className="booking-section">
      <header className="booking-header">
        <h2 className="booking-title">Bookings</h2>
        <button
          className="booking-btn booking-btn--primary"
          onClick={openCreate}
          data-testid="add-booking-btn"
        >
          + New Booking
        </button>
      </header>

      {showForm && (
        <form className="booking-form" onSubmit={handleSubmit} data-testid="booking-form">
          <h3 className="booking-form-title">New Booking</h3>

          <div className="booking-form-grid">
            <label className="booking-form-field">
              <span>Launch</span>
              <select
                value={form.launchId}
                onChange={(e) => handleFieldChange('launchId', e.target.value)}
                required
                data-testid="field-launch"
              >
                <option value="">Select a launch</option>
                {launches.map((l) => (
                  <option key={l.id} value={String(l.id)}>
                    {l.rocketName} — {l.date}
                  </option>
                ))}
              </select>
            </label>

            <label className="booking-form-field">
              <span>Passenger Name</span>
              <input
                type="text"
                value={form.passengerName}
                onChange={(e) => handleFieldChange('passengerName', e.target.value)}
                required
                data-testid="field-passenger-name"
              />
            </label>

            <label className="booking-form-field">
              <span>Passenger Email</span>
              <input
                type="email"
                value={form.passengerEmail}
                onChange={(e) => handleFieldChange('passengerEmail', e.target.value)}
                required
                data-testid="field-passenger-email"
              />
            </label>

            <label className="booking-form-field">
              <span>Passenger Phone</span>
              <input
                type="tel"
                value={form.passengerPhone}
                onChange={(e) => handleFieldChange('passengerPhone', e.target.value)}
                required
                data-testid="field-passenger-phone"
              />
            </label>
          </div>

          {formError && (
            <p className="booking-form-error" role="alert" data-testid="form-error">
              {formError}
            </p>
          )}

          <div className="booking-form-actions">
            <button
              type="button"
              className="booking-btn booking-btn--ghost"
              onClick={cancelForm}
              disabled={isSaving}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="booking-btn booking-btn--primary"
              disabled={isSaving}
              data-testid="submit-btn"
            >
              {isSaving ? 'Saving…' : 'Add Booking'}
            </button>
          </div>
        </form>
      )}

      {bookings && bookings.length === 0 ? (
        <p className="booking-empty" data-testid="bookings-empty">
          No bookings yet. Add one to get started.
        </p>
      ) : (
        <div className="booking-table-wrapper">
          <table className="booking-table" data-testid="bookings-table">
            <thead>
              <tr>
                <th>Passenger</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Launch</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {bookings?.map((booking) => (
                <tr key={booking.id} data-testid={`booking-row-${booking.id}`}>
                  <td className="booking-passenger">{booking.passengerName}</td>
                  <td>{booking.passengerEmail}</td>
                  <td>{booking.passengerPhone}</td>
                  <td>
                    {booking.launchRocketName} — {booking.launchDate}
                  </td>
                  <td>
                    <span
                      className={`booking-status booking-status--${booking.status.toLowerCase()}`}
                    >
                      {booking.status}
                    </span>
                  </td>
                  <td className="booking-actions">
                    {booking.status === 'CREATED' && (
                      <button
                        className="booking-btn booking-btn--danger"
                        onClick={() => handleCancel(booking.id)}
                        data-testid={`cancel-btn-${booking.id}`}
                      >
                        Cancel
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
