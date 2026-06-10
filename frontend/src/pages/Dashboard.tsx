// src/pages/Dashboard.tsx
import { useState, useEffect } from 'react';
import ListeCanaux from '../components/ListeCanaux';
import { fetchCanauxAPI, creerCanalAPI, type Canal, fetchCanauxByUserId } from '../services/canalService';
import {
    fetchMessagesByCanalAPI,
    envoyerMessageAPI,
    modifierMessageAPI,
    supprimerMessageAPI,
    type Message
} from '../services/messageService';
import { fetchUtilisateursAPI, type Utilisateur } from '../services/authService';

interface DashboardProps {
    user: Utilisateur;
    onLogout: () => void;
}

export default function Dashboard({ user, onLogout }: DashboardProps) {
    const [canaux, setCanaux] = useState<Canal[]>([]);
    const [selectedCanal, setSelectedCanal] = useState<Canal | null>(null);
    const [messages, setMessages] = useState<Message[]>([]);
    const [nouveauContenu, setNouveauContenu] = useState('');

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [nomNouveauCanal, setNomNouveauCanal] = useState('');
    const [descNouveauCanal, setDescNouveauCanal] = useState('');
    const [typeNouveauCanal, setTypeNouveauCanal] = useState('public');

    const [loadingCanaux, setLoadingCanaux] = useState(true);
    const [loadingMessages, setLoadingMessages] = useState(false);
    const [erreur, setErreur] = useState<string | null>(null);

    const [pseudos, setPseudos] = useState<{ [key: number]: string }>({});

    // Nouveaux états pour la gestion des membres du canal privé
    const [tousLesUtilisateurs, setTousLesUtilisateurs] = useState<Utilisateur[]>([]);
    const [membresSelectionnes, setMembresSelectionnes] = useState<number[]>([]);

    const chargerMessages = async () => {
        if (!selectedCanal) return;
        setLoadingMessages(true);
        try {
            const data = await fetchMessagesByCanalAPI(selectedCanal.idCanal);

            // On trie les messages du plus ancien au plus récent
            // 'a' et 'b' représentent deux messages que JavaScript compare
            // Si 'a' et 'b' ont été créé en même temps, on compare avec l'ID du message.
            data.sort((a, b) => (a.dateCreation - b.dateCreation) || (a.idMessage - b.idMessage));
            setMessages(data);
        } catch (err: any) {
            console.error(err);
        } finally {
            setLoadingMessages(false);
        }
    };

    // Chargement initial des canaux et des utilisateurs
    useEffect(() => {
        const chargerDonneesInitiales = async () => {
            try {
                // On lance le chargement des canaux et des utilisateurs en parallèle
                const [dataCanaux, dataUtilisateurs] = await Promise.all([
                    fetchCanauxAPI(),
                    // fetchCanauxByUserId(user.idUtilisateur),
                    fetchUtilisateursAPI()
                ]);

                setCanaux(dataCanaux);
                setTousLesUtilisateurs(dataUtilisateurs); // Sauvegarde de la liste complète des utilisateurs

                // On transforme le tableau d'utilisateurs en dictionnaire { id: pseudo }
                const dictionnairePseudos: { [key: number]: string } = {};
                dataUtilisateurs.forEach(u => {
                    dictionnairePseudos[u.idUtilisateur] = u.pseudo;
                });
                setPseudos(dictionnairePseudos);

            } catch (err: any) {
                setErreur(err.message);
            } finally {
                setLoadingCanaux(false);
            }
        };

        chargerDonneesInitiales();
    }, []);

    // Chargement automatique des messages quand le canal change
    useEffect(() => {
        chargerMessages();
    }, [selectedCanal]);

    // Gère l'ajout/retrait d'un utilisateur sélectionné pour le canal privé
    const handleToggleMembre = (idUtilisateur: number) => {
        setMembresSelectionnes((prev) =>
            prev.includes(idUtilisateur)
                ? prev.filter((id) => id !== idUtilisateur)
                : [...prev, idUtilisateur]
        );
    };

    // Action : Envoyer un message
    const handleSendMessage = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!selectedCanal || nouveauContenu.trim() === '') return;

        try {
            await envoyerMessageAPI(selectedCanal.idCanal, user.idUtilisateur, nouveauContenu);
            await chargerMessages();
            setNouveauContenu('');
        } catch (err) {
            console.error(err);
        }
    };

    // Action : Modifier un message (Uniquement l'auteur)
    const handleEditMessage = async (idMessage: number, contenuActuel: string) => {
        const nouveauTexte = prompt("Modifiez votre message :", contenuActuel);
        if (nouveauTexte === null || nouveauTexte.trim() === '' || nouveauTexte === contenuActuel) return;

        try {
            await modifierMessageAPI(idMessage, nouveauTexte);
            await chargerMessages();
        } catch (err) {
            alert("Erreur lors de la modification du message.");
        }
    };

    // Action : Supprimer un message (Auteur ou Admin du canal)
    const handleDeleteMessage = async (idMessage: number) => {
        if (!selectedCanal) return;

        if (!confirm("Voulez-vous vraiment supprimer ce message ?")) return;

        try {
            await supprimerMessageAPI(selectedCanal.idCanal, idMessage);
            await chargerMessages();
        } catch (err) {
            alert("Erreur lors de la suppression du message.");
        }
    };

    // Action : Créer un canal (et lui attribuer ses membres s'il est privé)
    const handleCreateCanal = async (e: React.FormEvent) => {
        e.preventDefault();
        if (nomNouveauCanal === '' || descNouveauCanal === '') return;

        try {
            // 1. Création du canal de base
            const canalCree = await creerCanalAPI(nomNouveauCanal, descNouveauCanal, typeNouveauCanal, user.idUtilisateur);

            // 2. Si le canal est privé, on lie les membres sélectionnés en parallèle
            if (typeNouveauCanal === 'privé' && membresSelectionnes.length > 0) {
                await Promise.all(
                    membresSelectionnes.map((idMembre) =>
                        fetch(`${import.meta.env.VITE_API_URL}/canaux/${canalCree.idCanal}/membres`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({ idUtilisateur: idMembre })
                        })
                    )
                );
            }

            // 3. Mise à jour des états et fermeture du modal
            setCanaux((prev) => [...prev, canalCree]);
            setIsModalOpen(false);
            setNomNouveauCanal('');
            setDescNouveauCanal('');
            setTypeNouveauCanal('public');
            setMembresSelectionnes([]); // Réinitialisation des membres cochés
        } catch (err) {
            console.error(err);
        }
    };

    return (
        <div className="dashboard-layout">
            {/* Barre latérale */}
            <aside className="sidebar">
                <h3>{user.pseudo}</h3>
                <button type="button" onClick={onLogout}>
                    Déconnexion
                </button>
                <hr/>

                {loadingCanaux && <p>Chargement des canaux...</p>}
                {erreur && <p>{erreur}</p>}

                {!loadingCanaux && !erreur && (
                    <ListeCanaux
                        canaux={canaux}
                        selectedCanalId={selectedCanal ? selectedCanal.idCanal : null}
                        onSelectCanal={(canal) => setSelectedCanal(canal)}
                    />
                )}

                <button type="button" onClick={() => setIsModalOpen(true)}>
                    Ajouter un canal
                </button>
            </aside>

            {/* Zone principale de discussion */}
            <main className="main-content">
                {selectedCanal ? (
                    <div className="chat-container">
                        <div className="chat-header">
                            <h2>{selectedCanal.nom}</h2>
                            <p><em>{selectedCanal.description || 'Aucune description'}</em></p>
                        </div>

                        <div className="messages-list">
                            {loadingMessages ? (
                                <p>Chargement des messages...</p>
                            ) : messages.length === 0 ? (
                                <p>Aucun message dans ce canal.</p>
                            ) : (
                                messages.map((msg) => {
                                    // Calcul des droits pour chaque message
                                    const estAuteur = msg.idUtilisateur === user.idUtilisateur;
                                    const estAdminCanal = selectedCanal.idAdmin === user.idUtilisateur;

                                    return (
                                        <div key={msg.idMessage} className="message-item">
                                            <div>
                                                <span className="message-author">
                                                    {pseudos[msg.idUtilisateur] || 'Inconnu'} :
                                                </span>
                                                <span className="message-text">{msg.contenu}</span>
                                            </div>

                                            {/* Zone d'actions contextuelle */}
                                            <div className="message-actions">
                                                {/* Seul l'auteur peut modifier */}
                                                {estAuteur && (
                                                    <button
                                                        type="button"
                                                        onClick={() => handleEditMessage(msg.idMessage, msg.contenu)}
                                                    >
                                                        Modifier
                                                    </button>
                                                )}

                                                {/* L'auteur OU l'admin du canal peut supprimer */}
                                                {(estAuteur || estAdminCanal) && (
                                                    <button
                                                        type="button"
                                                        className="btn-danger"
                                                        onClick={() => handleDeleteMessage(msg.idMessage)}
                                                    >
                                                        Supprimer
                                                    </button>
                                                )}
                                            </div>
                                        </div>
                                    );
                                })
                            )}
                        </div>

                        <form onSubmit={handleSendMessage} className="message-form">
                            <input
                                type="text"
                                placeholder={`Envoyer un message dans #${selectedCanal.slug}...`}
                                value={nouveauContenu}
                                onChange={(e) => setNouveauContenu(e.target.value)}
                            />
                            <button type="submit">Envoyer</button>
                        </form>
                    </div>
                ) : (
                    <div className="no-channel">
                        <h2>Zone de discussion</h2>
                        <p>Sélectionnez un canal à gauche pour afficher les messages.</p>
                    </div>
                )}
            </main>

            {isModalOpen && (
                <div>
                    Création d'un nouveau canal
                    <form onSubmit={handleCreateCanal} className="message-form">
                        <input
                            type="text"
                            placeholder={`Nom du nouveau canal`}
                            value={nomNouveauCanal}
                            onChange={(e) => setNomNouveauCanal(e.target.value)}
                        />
                        <input
                            type="text"
                            placeholder={`Description du nouveau canal`}
                            value={descNouveauCanal}
                            onChange={(e) => setDescNouveauCanal(e.target.value)}
                        />
                        <select value={typeNouveauCanal} onChange={(e) => setTypeNouveauCanal(e.target.value)}>
                            <option value="public">Public</option>
                            <option value="privé">Privé</option>
                        </select>

                        {/* Liste des cases à cocher affichée uniquement si le canal est configuré sur 'privé' */}
                        {typeNouveauCanal === 'privé' && (
                            <div className="membres-selection" style={{ margin: '15px 0', textAlign: 'left' }}>
                                <p><strong>Sélectionner les membres du canal privé :</strong></p>
                                <div style={{ maxHeight: '150px', overflowY: 'auto', border: '1px solid #ccc', padding: '10px' }}>
                                    {tousLesUtilisateurs
                                        .filter((u) => u.idUtilisateur !== user.idUtilisateur) // On exclut l'utilisateur actuel (créateur)
                                        .map((u) => (
                                            <label key={u.idUtilisateur} style={{ display: 'block', margin: '5px 0', cursor: 'pointer' }}>
                                                <input
                                                    type="checkbox"
                                                    checked={membresSelectionnes.includes(u.idUtilisateur)}
                                                    onChange={() => handleToggleMembre(u.idUtilisateur)}
                                                    style={{ marginRight: '8px' }}
                                                />
                                                {u.pseudo}
                                            </label>
                                        ))
                                    }
                                </div>
                            </div>
                        )}

                        <button type={"submit"}>Valider</button>
                    </form>
                    <button type={"button"} onClick={() => {
                        setIsModalOpen(false);
                        setMembresSelectionnes([]); // Reset en cas de fermeture manuelle
                    }}>Fermer</button>
                </div>
            )}
        </div>
    );
}