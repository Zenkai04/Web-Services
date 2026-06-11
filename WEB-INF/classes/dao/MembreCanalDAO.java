package dao;

import dto.Utilisateur;
import dto.Canal;
import java.util.List;

/**
 * CONTRAT DAO - Relation entre utilisateurs et canaux.
 *
 * Responsabilites :
 * - Lire les membres d'un canal.
 * - Lire les canaux suivis par un utilisateur.
 * - Ajouter ou retirer une appartenance.
 * - Verifier rapidement les droits d'acces lies a l'appartenance.
 */
public interface MembreCanalDAO {

    /**
     * Retourne les utilisateurs membres d'un canal.
     */
    List<Utilisateur> findByCanal(int idCanal);

    /**
     * Retourne les canaux auxquels appartient un utilisateur.
     */
    List<Canal> findByUtilisateur(int idUtilisateur);

    /**
     * Indique si un utilisateur est membre d'un canal.
     */
    boolean isMembre(int idUtilisateur, int idCanal);

    /**
     * Ajoute un utilisateur dans un canal.
     */
    boolean addMembre(int idUtilisateur, int idCanal);

    /**
     * Retire un utilisateur d'un canal.
     */
    boolean removeMembre(int idUtilisateur, int idCanal);
}
