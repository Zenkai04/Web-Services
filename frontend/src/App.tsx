// src/App.tsx
import { useState } from 'react';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import type { Utilisateur } from './services/authService';

function App() {
    // Au chargement, on regarde si un utilisateur est déjà sauvegardé dans le navigateur
    const [currentUser, setCurrentUser] = useState<Utilisateur | null>(() => {
        const savedUser = localStorage.getItem('journal_de_bord_session');
        return savedUser ? JSON.parse(savedUser) : null;
    });

    const [ecranActuel, setEcranActuel] = useState<'login' | 'register'>('login');

    // Action : Sauvegarder la session lors d'un succès
    const handleAuthSuccess = (user: Utilisateur) => {
        localStorage.setItem('journal_de_bord_session', JSON.stringify(user));
        setCurrentUser(user);
    };

    // Action : Supprimer la session lors de la déconnexion
    const handleLogout = () => {
        localStorage.removeItem('journal_de_bord_session');
        setCurrentUser(null);
        setEcranActuel('login');
    };

    // Si l'utilisateur est connecté, on ouvre le Dashboard
    if (currentUser) {
        return (
            <Dashboard
                user={currentUser}
                onLogout={handleLogout}
            />
        );
    }

    // S'il n'est pas connecté, gestion des écrans de connexion/inscription
    if (ecranActuel === 'register') {
        return (
            <Register
                onRegisterSuccess={handleAuthSuccess}
                onNavigateToLogin={() => setEcranActuel('login')}
            />
        );
    }

    return (
        <Login
            onLoginSuccess={handleAuthSuccess}
            onNavigateToRegister={() => setEcranActuel('register')}
        />
    );
}

export default App;