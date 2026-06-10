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
 * Récupère la liste des canaux depuis l'API Java
 */
export async function fetchCanauxAPI(): Promise<Canal[]> {
    const response = await fetch(`${BASE_URL}/canaux`);

    if (!response.ok) {
        throw new Error(`Erreur HTTP : ${response.status} - Impossible de charger les canaux`);
    }

    return await response.json();
}

export async function creerCanalAPI(nom: string, description: string, typeCanal: string, idAdmin: number): Promise<Canal> {
    const response = await fetch(`${BASE_URL}/canaux`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            nom,
            description,
            typeCanal,
            idAdmin
        }),
    });

    if (!response.ok) {
        throw new Error(`Erreur HTTP : ${response.status} - Impossible de créer le canal`);
    }

    return await response.json();
}