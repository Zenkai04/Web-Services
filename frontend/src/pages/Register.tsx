import { useState } from 'react';
import { inscriptionAPI, type Utilisateur } from '../services/authService';

interface RegisterProps {
    onRegisterSuccess: (user: Utilisateur) => void;
    onNavigateToLogin: () => void;
}

export default function Register({ onRegisterSuccess, onNavigateToLogin }: RegisterProps) {
    const [pseudo, setPseudo] = useState('');
    const [email, setEmail] = useState('');
    const [motDePasse, setMotDePasse] = useState('');
    const [erreur, setErreur] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErreur(null);

        if (!pseudo.trim() || !email.trim() || !motDePasse.trim()) {
            setErreur('Tous les champs sont obligatoires pour créer un compte.');
            return;
        }

        setLoading(true);

        try {
            const user = await inscriptionAPI(pseudo, email, motDePasse);
            onRegisterSuccess(user);
        } catch (err: any) {
            setErreur(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-page register-page">
            <h2>Créer un nouveau compte</h2>
            <p>Rejoignez le Journal de Bord dès aujourd'hui !</p>

            <form onSubmit={handleSubmit} className="auth-form">
                {erreur && <p className="error-message">{erreur}</p>}

                <div className="auth-fields">
                    <input
                        type="text"
                        placeholder="Choisissez un pseudo..."
                        value={pseudo}
                        onChange={(e) => setPseudo(e.target.value)}
                        disabled={loading}
                    />

                    <input
                        type="email"
                        placeholder="Votre adresse email..."
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        disabled={loading}
                    />

                    <input
                        type="password"
                        placeholder="Créez un mot de passe sécurisé..."
                        value={motDePasse}
                        onChange={(e) => setMotDePasse(e.target.value)}
                        disabled={loading}
                    />

                    <button type="submit" disabled={loading} className="btn-success">
                        {loading ? "Inscription en cours..." : "Finaliser l'inscription"}
                    </button>
                </div>
            </form>

            <div className="auth-footer">
                <span>Déjà un compte ? </span>
                <button type="button" onClick={onNavigateToLogin} disabled={loading} className="btn-link">
                    Retour à la connexion
                </button>
            </div>
        </div>
    );
}