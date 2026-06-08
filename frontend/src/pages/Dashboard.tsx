// src/pages/Dashboard.tsx
import { useState, useEffect } from 'react';
import ListeCanaux from '../components/ListeCanaux';
import { fetchCanauxAPI, type Canal } from '../services/canalService';
import {
    fetchMessagesByCanalAPI,
    envoyerMessageAPI,
    modifierMessageAPI,
    supprimerMessageAPI,
    MOCK_PSEUDOS,
    type Message
} from '../services/messageService';
import type { Utilisateur } from '../services/authService';

interface DashboardProps {
    user: Utilisateur;
    onLogout: () => void;
}

export default function Dashboard({ user, onLogout }: DashboardProps) {
    const [canaux, setCanaux] = useState<Canal[]>([]);
    const [selectedCanal, setSelectedCanal] = useState<Canal | null>(null);
    const [messages, setMessages] = useState<Message[]>([]);
    const [nouveauContenu, setNouveauContenu] = useState('');

    const [loadingCanaux, setLoadingCanaux] = useState(true);
    const [loadingMessages, setLoadingMessages] = useState(false);
    const [erreur, setErreur] = useState<string | null>(null);

    // Chargement initial des canaux
    useEffect(() => {
        const chargerCanaux = async () => {
            try {
                const data = await fetchCanauxAPI();
                setCanaux(data);
            } catch (err: any) {
                setErreur(err.message);
            } finally {
                setLoadingCanaux(false);
            }
        };
        chargerCanaux();
    }, []);

    // Chargement automatique des messages quand le canal change
    useEffect(() => {
        if (!selectedCanal) return;

        const chargerMessages = async () => {
            setLoadingMessages(true);
            try {
                const data = await fetchMessagesByCanalAPI(selectedCanal.idCanal);
                setMessages(data);
            } catch (err: any) {
                console.error(err);
            } finally {
                setLoadingMessages(false);
            }
        };

        chargerMessages();
    }, [selectedCanal]);

    // Action : Envoyer un message
    const handleSendMessage = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!selectedCanal || nouveauContenu.trim() === '') return;

        try {
            const msgCree = await envoyerMessageAPI(selectedCanal.idCanal, user.idUtilisateur, nouveauContenu);
            setMessages((prev) => [...prev, msgCree]);
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
            // Mise à jour de l'affichage local (on remplace le contenu du message modifié)
            setMessages((prev) =>
                prev.map((msg) => msg.idMessage === idMessage ? { ...msg, contenu: nouveauTexte } : msg)
            );
        } catch (err) {
            alert("Erreur lors de la modification du message.");
        }
    };

    // Action : Supprimer un message (Auteur ou Admin du canal)
    const handleDeleteMessage = async (idMessage: number) => {
        if (!confirm("Voulez-vous vraiment supprimer ce message ?")) return;

        try {
            await supprimerMessageAPI(idMessage);
            // Mise à jour de l'affichage local (on retire le message de la liste)
            setMessages((prev) => prev.filter((msg) => msg.idMessage !== idMessage));
        } catch (err) {
            alert("Erreur lors de la suppression du message.");
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
                                                    {MOCK_PSEUDOS[msg.idUtilisateur] || 'Inconnu'} :
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
        </div>
    );
}