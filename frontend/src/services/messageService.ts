// src/services/messageService.ts

export interface Message {
    idMessage: number;
    idUtilisateur: number;
    idCanal: number;
    contenu: string;
    dateCreation: number;
    dateModification: string | null;
}

const BASE_URL = import.meta.env.VITE_API_URL;

/**
 * API - Récupère les messages d'un canal spécifique
 */
export async function fetchMessagesByCanalAPI(idCanal: number, token: string): Promise<Message[]> {
    const response = await fetch(`${BASE_URL}/canaux/${idCanal}/messages`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        throw new Error(`Impossible de charger les messages du canal ${idCanal}`);
    }

    return await response.json();
}

/**
 * API - Envoie un message dans un canal
 */
export async function envoyerMessageAPI(idCanal: number, idUtilisateur: number, contenu: string, token: string): Promise<Message> {
    const response = await fetch(`${BASE_URL}/canaux/${idCanal}/messages`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ idCanal, idUtilisateur, contenu })
    });

    if (!response.ok) {
        throw new Error("Erreur lors de l'envoi du message");
    }

    return await response.json();
}

/**
 * API - Modifier le contenu d'un message
 */
export async function modifierMessageAPI(idMessage: number, nouveauContenu: string, token: string): Promise<void> {
    const response = await fetch(`${BASE_URL}/messages/${idMessage}`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ contenu: nouveauContenu })
    });

    if (!response.ok) {
        throw new Error('Erreur lors de la modification du message');
    }
}

/**
 * API - Supprimer un message
 */
export async function supprimerMessageAPI(idCanal: number, idMessage: number, token: string): Promise<void> {
    const response = await fetch(`${BASE_URL}/canaux/${idCanal}/messages/${idMessage}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        throw new Error('Erreur lors de la suppression du message');
    }
}