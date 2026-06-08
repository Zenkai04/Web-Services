// src/services/authService.ts

export interface Utilisateur {
    idUtilisateur: number;
    pseudo: string;
    email: string;
    motDePasseHash: string;
    dateInscription: string;
}

const MOCK_USERS: Utilisateur[] = [
    { idUtilisateur: 1, pseudo: 'alice', email: 'alice@email.com', motDePasseHash: 'alice123', dateInscription: '2026-06-04T10:00:00' },
    { idUtilisateur: 2, pseudo: 'bob', email: 'bob@email.com', motDePasseHash: 'bob123', dateInscription: '2026-06-04T10:05:00' },
    { idUtilisateur: 3, pseudo: 'charlie', email: 'charlie@email.com', motDePasseHash: 'charlie123', dateInscription: '2026-06-04T10:10:00' }
];

const BASE_URL = import.meta.env.VITE_API_URL;

/**
 * API - Récupère tous les utilisateurs pour associer les IDs aux pseudos
 */
export async function fetchUtilisateursAPI(): Promise<Utilisateur[]> {
    const response = await fetch(`${BASE_URL}/utilisateurs`);

    if (!response.ok) {
        throw new Error("Impossible de charger la liste des utilisateurs");
    }

    return await response.json();
}

export async function loginAPI(pseudo: string, motDePasse: string): Promise<Utilisateur> {
    // -------------------------------------------------------------------------
    // EN ATTENDANT LE BACKEND : On simule la réponse HTTP du serveur Jakarta
    // -------------------------------------------------------------------------
    return new Promise((resolve, reject) => {
        setTimeout(() => {
            const user = MOCK_USERS.find(
                (u) => u.pseudo.toLowerCase() === pseudo.toLowerCase().trim() && u.motDePasseHash === motDePasse
            );

            if (user) {
                resolve(user); // Équivalent d'un HTTP 200 OK avec le JSON
            } else {
                reject(new Error('Pseudo ou mot de passe incorrect (HTTP 401)')); // Équivalent d'un HTTP 401 Unauthorized
            }
        }, 800);
    });

    /* // PRESTISSMO : Quand ton binôme a fini sa Servlet, tu supprimeras le bloc du dessus
    // et tu décommenteras simplement ce bloc ci-dessous !

    const response = await fetch('http://localhost:8080/web-services/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pseudo, motDePasse })
    });

    if (!response.ok) {
        throw new Error('Pseudo ou mot de passe incorrect');
    }

    return await response.json();
    */
}