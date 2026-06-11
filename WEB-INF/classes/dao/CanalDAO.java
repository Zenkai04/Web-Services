package dao;

import dto.Canal;
import java.util.List;

/**
 * CONTRAT DAO - Acces aux canaux.
 *
 * Responsabilites :
 * - Decrire les operations disponibles sur la table canal.
 * - Masquer la technologie de persistance aux controleurs REST.
 * - Centraliser les recherches utilisees par les regles de visibilite.
 */
public interface CanalDAO {

    /**
     * Retourne tous les canaux existants, sans filtrage de securite.
     */
    List<Canal> findAll();

    /**
     * Retourne les canaux auxquels un utilisateur appartient.
     */
    List<Canal> findByUtilisateurId(int idUtilisateur);

    /**
     * Recherche un canal par son identifiant technique.
     */
    Canal findById(int idCanal);

    /**
     * Recherche un canal par son slug public.
     */
    Canal findBySlug(String slug);

    /**
     * Cree un canal et renseigne son identifiant genere si l'insertion reussit.
     */
    boolean save(Canal canal);

    /**
     * Met a jour les informations principales d'un canal.
     */
    boolean update(Canal canal);
}
