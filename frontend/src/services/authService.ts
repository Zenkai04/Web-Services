// src/services/authService.ts

export interface Utilisateur {
    idUtilisateur: number;
    pseudo: string;
    email: string;
    motDePasseHash?: string;
    dateInscription?: string;
    token?: string;
}

const BASE_URL = import.meta.env.VITE_API_URL;

/**
 * API - Récupère tous les utilisateurs depuis le serveur Java (Sécurisé)
 */
export async function fetchUtilisateursAPI(token: string): Promise<Utilisateur[]> {
    const response = await fetch(`${BASE_URL}/utilisateurs`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        throw new Error("Impossible de charger la liste des utilisateurs");
    }

    return await response.json();
}

/**
 * API - Connexion de l'utilisateur et récupération du jeton JWT
 */
export async function loginAPI(pseudo: string, motDePasse: string): Promise<Utilisateur> {
    const response = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            pseudo: pseudo.trim(),
            motDePasse: motDePasse
        })
    });

    if (!response.ok) {
        if (response.status === 401) {
            throw new Error('Pseudo ou mot de passe incorrect');
        }
        throw new Error(`Erreur de connexion (Code ${response.status})`);
    }

    const data = await response.json();
    const profilPublic = data.user || data.utilisateur || data.utilisateurPublic;

    if (!profilPublic) {
        throw new Error("Impossible de récupérer les informations de l'utilisateur dans AuthResponse");
    }

    return {
        idUtilisateur: profilPublic.idUtilisateur,
        pseudo: profilPublic.pseudo,
        email: profilPublic.email,
        token: data.token
    };
}

/**
 * API - Création d'un nouveau compte
 */
export async function inscriptionAPI(pseudo: string, email: string, motDePasse: string): Promise<Utilisateur> {
    const response = await fetch(`${BASE_URL}/utilisateurs`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            pseudo: pseudo.trim(),
            email: email.trim(),
            motDePasseHash: motDePasse
        }),
    });

    if (!response.ok) {
        if (response.status === 409) {
            throw new Error('Ce pseudo ou cette adresse email est déjà utilisé.');
        }
        throw new Error(`Erreur lors de la création du compte (Code ${response.status})`);
    }

    return await response.json();
}