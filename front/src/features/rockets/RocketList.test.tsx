import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { RocketList } from './RocketList';
import { getRockets, createRocket, updateRocket, deleteRocket } from './rocketsApi';
import type { Rocket } from '../../shared/types/rocket';

vi.mock('./rocketsApi', () => ({
  getRockets: vi.fn(),
  createRocket: vi.fn(),
  updateRocket: vi.fn(),
  deleteRocket: vi.fn(),
}));

const falcon: Rocket = {
  id: 1,
  name: 'Falcon 9',
  capacity: 9,
  rangeKm: 200_000,
  status: 'ACTIVE',
  lastMaintenanceDate: '2026-01-15',
  nextMaintenanceDate: '2026-07-15',
};

beforeEach(() => {
  vi.mocked(getRockets).mockReset();
  vi.mocked(createRocket).mockReset();
  vi.mocked(updateRocket).mockReset();
  vi.mocked(deleteRocket).mockReset();
});

test('shows a loading indicator while fetching', () => {
  vi.mocked(getRockets).mockReturnValue(new Promise(() => {}));

  render(<RocketList />);

  expect(screen.getByTestId('rockets-loading')).toBeInTheDocument();
});

test('renders the fleet table when data loads', async () => {
  vi.mocked(getRockets).mockResolvedValue([falcon]);

  render(<RocketList />);

  expect(await screen.findByTestId('rockets-table')).toBeInTheDocument();
  expect(screen.getByTestId('rocket-row-1')).toBeInTheDocument();
  expect(screen.getByText('Falcon 9')).toBeInTheDocument();
  expect(screen.getByText('ACTIVE')).toBeInTheDocument();
});

test('shows empty state when fleet has no rockets', async () => {
  vi.mocked(getRockets).mockResolvedValue([]);

  render(<RocketList />);

  expect(await screen.findByTestId('rockets-empty')).toBeInTheDocument();
});

test('shows error state when fetch fails', async () => {
  vi.mocked(getRockets).mockRejectedValue(new Error('network down'));

  render(<RocketList />);

  const error = await screen.findByTestId('rockets-error');
  expect(error).toHaveAttribute('role', 'alert');
  expect(error).toHaveTextContent(/network down/i);
});

test('opens the form when add button is clicked', async () => {
  vi.mocked(getRockets).mockResolvedValue([]);
  const user = userEvent.setup();

  render(<RocketList />);
  await screen.findByTestId('rockets-empty');

  await user.click(screen.getByTestId('add-rocket-btn'));

  expect(screen.getByTestId('rocket-form')).toBeInTheDocument();
  expect(screen.getByTestId('field-name')).toBeInTheDocument();
});

test('creates a rocket when form is submitted', async () => {
  vi.mocked(getRockets).mockResolvedValue([]);
  vi.mocked(createRocket).mockResolvedValue(falcon);
  vi.mocked(getRockets).mockResolvedValueOnce([]).mockResolvedValueOnce([falcon]);
  const user = userEvent.setup();

  render(<RocketList />);
  await screen.findByTestId('rockets-empty');

  await user.click(screen.getByTestId('add-rocket-btn'));
  await user.type(screen.getByTestId('field-name'), 'Falcon 9');
  await user.clear(screen.getByTestId('field-capacity'));
  await user.type(screen.getByTestId('field-capacity'), '9');
  await user.clear(screen.getByTestId('field-range'));
  await user.type(screen.getByTestId('field-range'), '200000');
  await user.click(screen.getByTestId('submit-btn'));

  await waitFor(() => expect(createRocket).toHaveBeenCalled());
  expect(createRocket).toHaveBeenCalledWith(
    expect.objectContaining({ name: 'Falcon 9', capacity: 9, rangeKm: 200_000 }),
  );
});

test('opens edit form pre-filled with rocket data', async () => {
  vi.mocked(getRockets).mockResolvedValue([falcon]);
  const user = userEvent.setup();

  render(<RocketList />);
  await screen.findByTestId('rockets-table');

  await user.click(screen.getByTestId('edit-btn-1'));

  expect(screen.getByTestId('rocket-form')).toBeInTheDocument();
  expect(screen.getByTestId('field-name')).toHaveValue('Falcon 9');
  expect(screen.getByTestId('field-status')).toHaveValue('ACTIVE');
});

test('shows confirmation before deleting a rocket', async () => {
  vi.mocked(getRockets).mockResolvedValue([falcon]);
  vi.mocked(deleteRocket).mockResolvedValue(undefined);
  vi.mocked(getRockets).mockResolvedValueOnce([falcon]).mockResolvedValueOnce([]);
  const user = userEvent.setup();

  render(<RocketList />);
  await screen.findByTestId('rockets-table');

  await user.click(screen.getByTestId('delete-btn-1'));
  expect(screen.getByTestId('confirm-delete-1')).toBeInTheDocument();

  await user.click(screen.getByTestId('confirm-delete-1'));
  await waitFor(() => expect(deleteRocket).toHaveBeenCalledWith(1));
});
