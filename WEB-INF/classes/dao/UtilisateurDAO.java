package dao;

import dto.Utilisateur;
import java.util.List;

/**
 * CONTRAT DAO - Acces aux utilisateurs.
 *
 * Responsabilites :
 * - Rechercher les utilisateurs par identifiant, pseudo ou email.
 * - Creer et modifier les comptes.
 * - Fournir les donnees completes aux couches d'authentification et de securite.
 */
public interface UtilisateurDAO {

    /**
     * Retourne tous les utilisateurs.
     */
    List<Utilisateur> findAll();

    /**
     * Recherche un utilisateur par identifiant.
     */
    Utilisateur findById(int idUtilisateur);

    /**
     * Recherche un utilisateur par pseudo, notamment pour la connexion.
     */
    Utilisateur findByPseudo(String pseudo);

    /**
     * Recherche un utilisateur par email.
     */
    Utilisateur findByEmail(String email);

    /**
     * Cree un utilisateur et renseigne son identifiant genere si l'insertion reussit.
     */
    boolean save(Utilisateur utilisateur);

    /**
     * Met a jour les informations d'un utilisateur.
     */
    boolean update(Utilisateur utilisateur);
}
