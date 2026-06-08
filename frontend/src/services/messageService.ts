// src/services/messageService.ts

export interface Message {
    idMessage: number;
    idUtilisateur: number;
    idCanal: number;
    contenu: string;
    dateCreation: string;
    dateModification: string | null;
}

// Faux dictionnaire pour lier les ID aux pseudos à l'affichage (simule une jointure SQL)
export const MOCK_PSEUDOS: { [key: number]: string } = {
    1: 'alice',
    2: 'bob',
    3: 'charlie'
};

// 10 messages jouets configurés exactement selon ton jalon 3 d'alimentation SQL
const MOCK_MESSAGES: Message[] = [
    { idMessage: 1, idUtilisateur: 1, idCanal: 10, contenu: 'Bonjour tout le monde et bienvenue sur le canal général !', dateCreation: '2026-06-04T10:00:00', dateModification: null },
    { idMessage: 2, idUtilisateur: 2, idCanal: 10, contenu: 'Salut Alice ! Ravi de voir que l\'application avance bien.', dateCreation: '2026-06-04T10:01:00', dateModification: null },
    { idMessage: 3, idUtilisateur: 3, idCanal: 10, contenu: 'Hello à tous ! Super ce nouveau système de messagerie.', dateCreation: '2026-06-04T10:02:00', dateModification: null },
    { idMessage: 4, idUtilisateur: 1, idCanal: 10, contenu: 'Merci Charlie. N\'hésite pas à remonter des bugs ici si tu en trouves.', dateCreation: '2026-06-04T10:03:00', dateModification: null },
    { idMessage: 5, idUtilisateur: 3, idCanal: 10, contenu: 'Est-ce que ce canal est visible par les invités externes ?', dateCreation: '2026-06-04T10:04:00', dateModification: null },
    { idMessage: 6, idUtilisateur: 2, idCanal: 10, contenu: 'Oui Charlie, comme c\'est un canal "public", tous les inscrits y ont accès.', dateCreation: '2026-06-04T10:05:00', dateModification: null },

    { idMessage: 7, idUtilisateur: 2, idCanal: 20, contenu: 'Salut Alice, j\'ai créé ce canal privé pour qu\'on parle du budget.', dateCreation: '2026-06-04T10:06:00', dateModification: null },
    { idMessage: 8, idUtilisateur: 1, idCanal: 20, contenu: 'Parfait Bob. Est-ce que tu as bien vérifié que Charlie n\'a pas accès ?', dateCreation: '2026-06-04T10:07:00', dateModification: null },
    { idMessage: 9, idUtilisateur: 2, idCanal: 20, contenu: 'Je confirme, il n\'est pas dans la table membre_de pour ce canal.', dateCreation: '2026-06-04T10:08:00', dateModification: null },
    { idMessage: 10, idUtilisateur: 1, idCanal: 20, contenu: 'Super, on est tranquilles pour bosser. Je t\'envoie les chiffres cet après-midi.', dateCreation: '2026-06-04T10:09:00', dateModification: null }
];

/**
 * API - Récupère les messages d'un canal spécifique (Phase 2 Jalon 1)
 */
export async function fetchMessagesByCanalAPI(idCanal: number): Promise<Message[]> {
    return new Promise((resolve) => {
        setTimeout(() => {
            // On filtre pour ne renvoyer que les messages du canal actif
            const filtrés = MOCK_MESSAGES.filter((m) => m.idCanal === idCanal);
            resolve(filtrés);
        }, 300);
    });
}

/**
 * API - Envoie un message dans un canal (Phase 2 Jalon 1)
 */
export async function envoyerMessageAPI(idCanal: number, idUtilisateur: number, contenu: string): Promise<Message> {
    return new Promise((resolve) => {
        setTimeout(() => {
            const nouveauMessage: Message = {
                idMessage: Math.floor(Math.random() * 10000), // ID temporaire aléatoire
                idUtilisateur,
                idCanal,
                contenu,
                dateCreation: new Date().toISOString(),
                dateModification: null
            };
            resolve(nouveauMessage);
        }, 200);
    });
}

/**
 * API - Modifier le contenu d'un message
 */
export async function modifierMessageAPI(idMessage: number, nouveauContenu: string): Promise<void> {
    return new Promise((resolve) => {
        setTimeout(() => {
            console.log(`Message ${idMessage} modifié sur le serveur.`);
            resolve(); // Simule un HTTP 200 OK ou 24
        }, 200);
    });

    /* // Quand le backend sera prêt :
    const response = await fetch(`http://localhost:8080/web-services/api/messages/${idMessage}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ contenu: nouveauContenu })
    });
    if (!response.ok) throw new Error('Erreur lors de la modification');
    */
}

/**
 * API - Supprimer un message
 */
export async function supprimerMessageAPI(idMessage: number): Promise<void> {
    return new Promise((resolve) => {
        setTimeout(() => {
            console.log(`Message ${idMessage} supprimé sur le serveur.`);
            resolve(); // Simule un HTTP 200 OK ou 204 No Content
        }, 200);
    });

    /* // Quand le backend sera prêt :
    const response = await fetch(`http://localhost:8080/web-services/api/messages/${idMessage}`, {
        method: 'DELETE'
    });
    if (!response.ok) throw new Error('Erreur lors de la suppression');
    */
}