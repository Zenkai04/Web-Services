// src/services/canalService.ts

export interface Canal {
    idCanal: number;
    idAdmin: number;
    nom: string;
    description: string;
    typeCanal: string; // 'public' ou 'privé'
    slug: string;
    dateCreation: string;
}

/**
 * Récupère la liste des canaux depuis la vraie API Java (Tomcat)
 */
export async function fetchCanauxAPI(): Promise<Canal[]> {
    const response = await fetch('http://localhost:8080/web_services_temp_war_exploded/canaux');

    if (!response.ok) {
        throw new Error(`Erreur HTTP : ${response.status} - Impossible de charger les canaux`);
    }

    return await response.json();
}