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