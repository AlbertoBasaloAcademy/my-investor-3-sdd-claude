import { useState } from 'react';
import { useRockets } from './useRockets';
import { createRocket, updateRocket, deleteRocket } from './rocketsApi';
import type { Rocket, RocketRequest, RocketStatus, RocketRange } from '../../shared/types/rocket';
import './RocketList.css';

type FormState = {
  name: string;
  capacity: string;
  range: RocketRange;
  status: RocketStatus;
  lastMaintenanceDate: string;
  nextMaintenanceDate: string;
};

const EMPTY_FORM: FormState = {
  name: '',
  capacity: '',
  range: 'EARTH',
  status: 'ACTIVE',
  lastMaintenanceDate: '',
  nextMaintenanceDate: '',
};

function rocketToForm(rocket: Rocket): FormState {
  return {
    name: rocket.name,
    capacity: String(rocket.capacity),
    range: rocket.range,
    status: rocket.status,
    lastMaintenanceDate: rocket.lastMaintenanceDate ?? '',
    nextMaintenanceDate: rocket.nextMaintenanceDate ?? '',
  };
}

function formToRequest(form: FormState): RocketRequest {
  return {
    name: form.name,
    capacity: Number(form.capacity),
    range: form.range,
    status: form.status,
    lastMaintenanceDate: form.lastMaintenanceDate || null,
    nextMaintenanceDate: form.nextMaintenanceDate || null,
  };
}

export function RocketList() {
  const { data: rockets, error, isLoading, refresh } = useRockets();
  const [editingId, setEditingId] = useState<number | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [confirmDeleteId, setConfirmDeleteId] = useState<number | null>(null);
  const [formError, setFormError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  function openCreate() {
    setEditingId(null);
    setForm(EMPTY_FORM);
    setFormError(null);
    setShowForm(true);
  }

  function openEdit(rocket: Rocket) {
    setEditingId(rocket.id);
    setForm(rocketToForm(rocket));
    setFormError(null);
    setShowForm(true);
  }

  function cancelForm() {
    setShowForm(false);
    setEditingId(null);
    setFormError(null);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setFormError(null);
    setIsSaving(true);
    try {
      if (editingId !== null) {
        await updateRocket(editingId, formToRequest(form));
      } else {
        await createRocket(formToRequest(form));
      }
      setShowForm(false);
      setEditingId(null);
      refresh();
    } catch (err) {
      setFormError(err instanceof Error ? err.message : 'Operation failed');
    } finally {
      setIsSaving(false);
    }
  }

  async function handleDelete(id: number) {
    try {
      await deleteRocket(id);
      setConfirmDeleteId(null);
      refresh();
    } catch (err) {
      setConfirmDeleteId(null);
    }
  }

  function handleFieldChange(field: keyof FormState, value: string) {
    setForm((prev) => ({ ...prev, [field]: value }));
  }

  if (isLoading) {
    return (
      <section className="rocket-section" aria-busy="true">
        <p className="rocket-loading" data-testid="rockets-loading">
          <span className="rocket-spinner" aria-hidden="true" />
          Loading fleet data…
        </p>
      </section>
    );
  }

  if (error) {
    return (
      <section className="rocket-section">
        <p className="rocket-error" data-testid="rockets-error" role="alert">
          Failed to load fleet — {error.message}
        </p>
      </section>
    );
  }

  return (
    <section className="rocket-section">
      <header className="rocket-header">
        <h2 className="rocket-title">Rocket Fleet</h2>
        <button className="rocket-btn rocket-btn--primary" onClick={openCreate} data-testid="add-rocket-btn">
          + New Rocket
        </button>
      </header>

      {showForm && (
        <form className="rocket-form" onSubmit={handleSubmit} data-testid="rocket-form">
          <h3 className="rocket-form-title">{editingId !== null ? 'Edit Rocket' : 'New Rocket'}</h3>

          <div className="rocket-form-grid">
            <label className="rocket-form-field">
              <span>Name</span>
              <input
                type="text"
                value={form.name}
                onChange={(e) => handleFieldChange('name', e.target.value)}
                placeholder="e.g. Falcon 9"
                required
                data-testid="field-name"
              />
            </label>

            <label className="rocket-form-field">
              <span>Capacity (passengers)</span>
              <input
                type="number"
                min="1"
                value={form.capacity}
                onChange={(e) => handleFieldChange('capacity', e.target.value)}
                required
                data-testid="field-capacity"
              />
            </label>

            <label className="rocket-form-field">
              <span>Range</span>
              <select
                value={form.range}
                onChange={(e) => handleFieldChange('range', e.target.value as RocketRange)}
                data-testid="field-range"
              >
                <option value="EARTH">Earth</option>
                <option value="MOON">Moon</option>
                <option value="MARS">Mars</option>
              </select>
            </label>

            <label className="rocket-form-field">
              <span>Status</span>
              <select
                value={form.status}
                onChange={(e) => handleFieldChange('status', e.target.value as RocketStatus)}
                data-testid="field-status"
              >
                <option value="ACTIVE">Active</option>
                <option value="MAINTENANCE">Maintenance</option>
                <option value="RETIRED">Retired</option>
              </select>
            </label>

            <label className="rocket-form-field">
              <span>Last Maintenance</span>
              <input
                type="date"
                value={form.lastMaintenanceDate}
                onChange={(e) => handleFieldChange('lastMaintenanceDate', e.target.value)}
                data-testid="field-last-maintenance"
              />
            </label>

            <label className="rocket-form-field">
              <span>Next Maintenance</span>
              <input
                type="date"
                value={form.nextMaintenanceDate}
                onChange={(e) => handleFieldChange('nextMaintenanceDate', e.target.value)}
                data-testid="field-next-maintenance"
              />
            </label>
          </div>

          {formError && (
            <p className="rocket-form-error" role="alert" data-testid="form-error">
              {formError}
            </p>
          )}

          <div className="rocket-form-actions">
            <button type="button" className="rocket-btn rocket-btn--ghost" onClick={cancelForm} disabled={isSaving}>
              Cancel
            </button>
            <button type="submit" className="rocket-btn rocket-btn--primary" disabled={isSaving} data-testid="submit-btn">
              {isSaving ? 'Saving…' : editingId !== null ? 'Save Changes' : 'Add Rocket'}
            </button>
          </div>
        </form>
      )}

      {rockets && rockets.length === 0 ? (
        <p className="rocket-empty" data-testid="rockets-empty">
          No rockets in the fleet yet. Add one to get started.
        </p>
      ) : (
        <div className="rocket-table-wrapper">
          <table className="rocket-table" data-testid="rockets-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Capacity</th>
                <th>Range</th>
                <th>Status</th>
                <th>Last Maintenance</th>
                <th>Next Maintenance</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {rockets?.map((rocket) => (
                <tr key={rocket.id} data-testid={`rocket-row-${rocket.id}`}>
                  <td className="rocket-name">{rocket.name}</td>
                  <td>{rocket.capacity}</td>
                  <td>{rocket.range}</td>
                  <td>
                    <span className={`rocket-status rocket-status--${rocket.status.toLowerCase()}`}>
                      {rocket.status}
                    </span>
                  </td>
                  <td>{rocket.lastMaintenanceDate ?? '—'}</td>
                  <td>{rocket.nextMaintenanceDate ?? '—'}</td>
                  <td className="rocket-actions">
                    {confirmDeleteId === rocket.id ? (
                      <span className="rocket-confirm">
                        <span>Delete?</span>
                        <button
                          className="rocket-btn rocket-btn--danger"
                          onClick={() => handleDelete(rocket.id)}
                          data-testid={`confirm-delete-${rocket.id}`}
                        >
                          Yes
                        </button>
                        <button
                          className="rocket-btn rocket-btn--ghost"
                          onClick={() => setConfirmDeleteId(null)}
                        >
                          No
                        </button>
                      </span>
                    ) : (
                      <>
                        <button
                          className="rocket-btn rocket-btn--ghost"
                          onClick={() => openEdit(rocket)}
                          data-testid={`edit-btn-${rocket.id}`}
                        >
                          Edit
                        </button>
                        <button
                          className="rocket-btn rocket-btn--danger"
                          onClick={() => setConfirmDeleteId(rocket.id)}
                          data-testid={`delete-btn-${rocket.id}`}
                        >
                          Delete
                        </button>
                      </>
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
