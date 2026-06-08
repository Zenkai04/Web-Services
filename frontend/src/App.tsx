// src/App.tsx
import { useState } from 'react';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import type {Utilisateur} from './services/authService'; // On importe l'interface pour le type

function App() {
    const [currentUser, setCurrentUser] = useState<Utilisateur | null>(null);

    if (!currentUser) {
        return <Login onLoginSuccess={(user) => setCurrentUser(user)} />;
    }
    return (
        <Dashboard
            user={currentUser}
            onLogout={() => setCurrentUser(null)}
        />
    );
}

export default App;