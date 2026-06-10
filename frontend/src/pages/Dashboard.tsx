import { useState, useEffect } from 'react';
import ListeCanaux from '../components/ListeCanaux';
import { creerCanalAPI, type Canal, fetchCanauxByUserId } from '../services/canalService';
import {
    fetchMessagesByCanalAPI,
    envoyerMessageAPI,
    modifierMessageAPI,
    supprimerMessageAPI,
    type Message
} from '../services/messageService';
import { fetchUtilisateursAPI, type Utilisateur } from '../services/authService';

interface DashboardProps {
    user: Utilisateur & { token: string };
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
    const [tousLesUtilisateurs, setTousLesUtilisateurs] = useState<Utilisateur[]>([]);
    const [membresSelectionnes, setMembresSelectionnes] = useState<number[]>([]);

    /**
     * L'INTERCEPTEUR CENTRAL : Cette fonction englobe les requêtes API.
     * Si le serveur répond 401 (Token expiré/invalide), elle déconnecte l'utilisateur d'office.
     */
    const executerSecurise = async (action: () => Promise<void>) => {
        try {
            setErreur(null);
            await action();
        } catch (err: any) {
            console.error("Erreur interceptée :", err);

            if (err.message?.includes("401") || err.message?.toLowerCase().includes("token")) {
                alert("Votre session a expiré ou est invalide. Veuillez vous reconnecter.");
                onLogout();
            } else {
                setErreur(err.message || "Une erreur réseau est survenue.");
            }
        }
    };

    const chargerMessages = async () => {
        if (!selectedCanal) return;
        setLoadingMessages(true);

        // On utilise l'intercepteur pour emballer l'appel
        await executerSecurise(async () => {
            const data = await fetchMessagesByCanalAPI(selectedCanal.idCanal, user.token);
            data.sort((a, b) => (a.dateCreation - b.dateCreation) || (a.idMessage - b.idMessage));
            setMessages(data);
        });

        setLoadingMessages(false);
    };

    useEffect(() => {
        const chargerDonneesInitiales = async () => {
            await executerSecurise(async () => {
                const [dataCanaux, dataUtilisateurs] = await Promise.all([
                    fetchCanauxByUserId(user.idUtilisateur, user.token),
                    fetchUtilisateursAPI(user.token)
                ]);

                setCanaux(dataCanaux);
                setTousLesUtilisateurs(dataUtilisateurs);

                const dictionnairePseudos: { [key: number]: string } = {};
                dataUtilisateurs.forEach(u => {
                    dictionnairePseudos[u.idUtilisateur] = u.pseudo;
                });
                setPseudos(dictionnairePseudos);
            });

            setLoadingCanaux(false);
        };

        chargerDonneesInitiales();
    }, []);

    useEffect(() => {
        chargerMessages();
    }, [selectedCanal]);

    const handleToggleMembre = (idUtilisateur: number) => {
        setMembresSelectionnes((prev) =>
            prev.includes(idUtilisateur)
                ? prev.filter((id) => id !== idUtilisateur)
                : [...prev, idUtilisateur]
        );
    };

    const handleSendMessage = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!selectedCanal || nouveauContenu.trim() === '') return;

        await executerSecurise(async () => {
            await envoyerMessageAPI(selectedCanal.idCanal, user.idUtilisateur, nouveauContenu, user.token);
            await chargerMessages();
            setNouveauContenu('');
        });
    };

    const handleEditMessage = async (idMessage: number, contenuActuel: string) => {
        const nouveauTexte = prompt("Modifiez votre message :", contenuActuel);
        if (nouveauTexte === null || nouveauTexte.trim() === '' || nouveauTexte === contenuActuel) return;

        await executerSecurise(async () => {
            await modifierMessageAPI(idMessage, nouveauTexte, user.token);
            await chargerMessages();
        });
    };

    const handleDeleteMessage = async (idMessage: number) => {
        if (!selectedCanal) return;
        if (!confirm("Voulez-vous vraiment supprimer ce message ?")) return;

        await executerSecurise(async () => {
            await supprimerMessageAPI(selectedCanal.idCanal, idMessage, user.token);
            await chargerMessages();
        });
    };

    const handleCreateCanal = async (e: React.FormEvent) => {
        e.preventDefault();
        if (nomNouveauCanal === '' || descNouveauCanal === '') return;

        await executerSecurise(async () => {
            const canalCree = await creerCanalAPI(nomNouveauCanal, descNouveauCanal, typeNouveauCanal, user.idUtilisateur, user.token);

            if (typeNouveauCanal === 'privé') {
                const tousLesMembresAInscrire = [user.idUtilisateur, ...membresSelectionnes];

                await Promise.all(
                    tousLesMembresAInscrire.map((idMembre) =>
                        fetch(`${import.meta.env.VITE_API_URL}/canaux/${canalCree.idCanal}/membres`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                                'Authorization': `Bearer ${user.token}`
                            },
                            body: JSON.stringify({ idUtilisateur: idMembre })
                        })
                    )
                );
            }

            setCanaux((prev) => [...prev, canalCree]);
            setIsModalOpen(false);
            setNomNouveauCanal('');
            setDescNouveauCanal('');
            setTypeNouveauCanal('public');
            setMembresSelectionnes([]);
        });
    };

    return (
        <div className="dashboard-layout">
            <aside className="sidebar">
                <div className="sidebar-header">
                    <h3>{user.pseudo}</h3>
                    <button type="button" onClick={onLogout} className="btn-disconnect">
                        Déconnexion
                    </button>
                </div>
                <hr/>

                {loadingCanaux && <p>Chargement des canaux...</p>}
                {erreur && <p className="error-message">{erreur}</p>}

                {!loadingCanaux && !erreur && (
                    <ListeCanaux
                        canaux={canaux}
                        selectedCanalId={selectedCanal ? selectedCanal.idCanal : null}
                        onSelectCanal={(canal) => setSelectedCanal(canal)}
                    />
                )}

                <button type="button" onClick={() => setIsModalOpen(true)} className="btn-add-canal">
                    Ajouter un canal
                </button>
            </aside>

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
                                <p className="no-message">Aucun message dans ce canal.</p>
                            ) : (
                                messages.map((msg) => {
                                    const estAuteur = msg.idUtilisateur === user.idUtilisateur;
                                    const estAdminCanal = selectedCanal.idAdmin === user.idUtilisateur;

                                    return (
                                        <div key={msg.idMessage} className="message-item">
                                            <div className="message-body">
                                                <span className="message-author">
                                                    {pseudos[msg.idUtilisateur] || 'Inconnu'} :
                                                </span>
                                                <span className="message-text">{msg.contenu}</span>
                                            </div>

                                            <div className="message-actions">
                                                {estAuteur && (
                                                    <button type="button" onClick={() => handleEditMessage(msg.idMessage, msg.contenu)} className="btn-action">
                                                        Modifier
                                                    </button>
                                                )}
                                                {(estAuteur || estAdminCanal) && (
                                                    <button type="button" onClick={() => handleDeleteMessage(msg.idMessage)} className="btn-action btn-danger">
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
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>Création d'un nouveau canal</h3>
                        <form onSubmit={handleCreateCanal} className="modal-form">
                            <input
                                type="text"
                                placeholder="Nom du nouveau canal"
                                value={nomNouveauCanal}
                                onChange={(e) => setNomNouveauCanal(e.target.value)}
                            />
                            <input
                                type="text"
                                placeholder="Description du nouveau canal"
                                value={descNouveauCanal}
                                onChange={(e) => setDescNouveauCanal(e.target.value)}
                            />
                            <select value={typeNouveauCanal} onChange={(e) => setTypeNouveauCanal(e.target.value)}>
                                <option value="public">Public</option>
                                <option value="privé">Privé</option>
                            </select>

                            {typeNouveauCanal === 'privé' && (
                                <div className="membres-selection">
                                    <p><strong>Sélectionner les membres du canal privé :</strong></p>
                                    <div className="membres-scroll-list">
                                        {tousLesUtilisateurs
                                            .filter((u) => u.idUtilisateur !== user.idUtilisateur)
                                            .map((u) => (
                                                <label key={u.idUtilisateur} className="membre-checkbox-label">
                                                    <input
                                                        type="checkbox"
                                                        checked={membresSelectionnes.includes(u.idUtilisateur)}
                                                        onChange={() => handleToggleMembre(u.idUtilisateur)}
                                                    />
                                                    {u.pseudo}
                                                </label>
                                            ))
                                        }
                                    </div>
                                </div>
                            )}

                            <div className="modal-actions">
                                <button type="submit" className="btn-success">Valider</button>
                                <button type="button" onClick={() => {
                                    setIsModalOpen(false);
                                    setMembresSelectionnes([]);
                                }} className="btn-close">Fermer</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}