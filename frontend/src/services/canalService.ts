// src/services/canalService.ts

export interface Canal {
    idCanal: number;
    idAdmin: number;
    nom: string;
    description: string;
    typeCanal: string;
    slug: string;
    dateCreation: string;
}

const BASE_URL = import.meta.env.VITE_API_URL;

/**
 * Récupère la liste de tous les canaux (Sécurisé)
 */
export async function fetchCanauxAPI(token: string): Promise<Canal[]> {
    const response = await fetch(`${BASE_URL}/canaux`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        throw new Error(`Erreur HTTP : ${response.status} - Impossible de charger les canaux`);
    }

    return await response.json();
}

/**
 * Récupère la liste des canaux de l'utilisateur (Sécurisé)
 */
export async function fetchCanauxByUserId(idUtilisateur: number, token: string): Promise<Canal[]> {
    const response = await fetch(`${BASE_URL}/utilisateurs/${idUtilisateur}/canaux`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        throw new Error(`Erreur HTTP : ${response.status} - Impossible de charger les canaux`);
    }

    return await response.json();
}

/**
 * Crée un canal de discussion (Sécurisé)
 */
export async function creerCanalAPI(nom: string, description: string, typeCanal: string, idAdmin: number, token: string): Promise<Canal> {
    const response = await fetch(`${BASE_URL}/canaux`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ nom, description, typeCanal, idAdmin }),
    });

    if (!response.ok) {
        throw new Error(`Erreur HTTP : ${response.status} - Impossible de créer le canal`);
    }

    return await response.json();
}