import { HealthStatus } from './features/health/HealthStatus';
import { RocketList } from './features/rockets/RocketList';
import './App.css';

function App() {
  return (
    <main className="app-shell">
      <h1 className="app-hero">AstroBookings</h1>
      <HealthStatus />
      <RocketList />
    </main>
  );
}

export default App;
