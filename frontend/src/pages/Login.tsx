import { useState } from 'react';
import { loginAPI, type Utilisateur } from '../services/authService';

/**
 * PAGE LOGIN - Formulaire de connexion.
 *
 * Responsabilites :
 * - Collecter le pseudo et le mot de passe.
 * - Appeler le service d'authentification.
 * - Remonter la session au composant racine en cas de succes.
 */
interface LoginProps {
    onLoginSuccess: (user: Utilisateur) => void;
    onNavigateToRegister: () => void;
}

export default function Login({ onLoginSuccess, onNavigateToRegister }: LoginProps) {
    const [pseudo, setPseudo] = useState('');
    const [motDePasse, setMotDePasse] = useState('');
    const [erreur, setErreur] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    /**
     * Valide le formulaire puis tente la connexion via l'API backend.
     */
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErreur(null);

        if (!pseudo.trim() || !motDePasse.trim()) {
            setErreur('Veuillez remplir tous les champs.');
            return;
        }

        setLoading(true);

        try {
            const user = await loginAPI(pseudo, motDePasse);
            onLoginSuccess(user);
        } catch (err: any) {
            setErreur(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-page login-page">
            <h2>Connexion au Journal de Bord</h2>

            <form onSubmit={handleSubmit} className="auth-form">
                {erreur && <p className="error-message">{erreur}</p>}

                <div className="auth-fields">
                    <input
                        type="text"
                        placeholder="Entrez votre pseudo..."
                        value={pseudo}
                        onChange={(e) => setPseudo(e.target.value)}
                        disabled={loading}
                    />

                    <input
                        type="password"
                        placeholder="Entrez votre mot de passe..."
                        value={motDePasse}
                        onChange={(e) => setMotDePasse(e.target.value)}
                        disabled={loading}
                    />

                    <button type="submit" disabled={loading} className="btn-primary">
                        {loading ? 'Connexion en cours...' : 'Se connecter'}
                    </button>
                </div>
            </form>

            <div className="auth-footer">
                <p>Vous n'avez pas encore de compte ?</p>
                <button type="button" onClick={onNavigateToRegister} disabled={loading} className="btn-link">
                    Créer un compte
                </button>
            </div>
        </div>
    );
}
