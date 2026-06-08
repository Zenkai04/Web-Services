// src/pages/Login.tsx
import { useState } from 'react';
import { loginAPI, type Utilisateur } from '../services/authService';

interface LoginProps {
    onLoginSuccess: (user: Utilisateur) => void;
}

export default function Login({ onLoginSuccess }: LoginProps) {
    const [pseudo, setPseudo] = useState('');
    const [motDePasse, setMotDePasse] = useState('');
    const [erreur, setErreur] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErreur(null);

        if (!pseudo.trim() || !motDePasse.trim()) {
            setErreur('Veuillez remplir tous les champs.');
            return;
        }

        setLoading(true);

        try {
            // Appel direct à l'API (qui est actuellement branchée sur le mockup)
            const user = await loginAPI(pseudo, motDePasse);
            onLoginSuccess(user);
        } catch (err: any) {
            // On attrape l'erreur envoyée par le service (fausse ou vraie 401)
            setErreur(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h2>Connexion au Journal de Bord</h2>

            <form onSubmit={handleSubmit}>
                {erreur && <p>{erreur}</p>}

                <div>
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
                </div>

                <button type="submit" disabled={loading}>
                    {loading ? 'Connexion en cours...' : 'Entrer dans la messagerie'}
                </button>
            </form>
        </div>
    );
}