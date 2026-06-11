package dao;

import dto.Message;
import java.util.List;

/**
 * CONTRAT DAO - Acces aux messages.
 *
 * Responsabilites :
 * - Lire les messages globalement ou par canal.
 * - Creer et modifier un message.
 * - Supprimer un message lorsque la servlet CanalRestAPI l'autorise.
 */
public interface MessageDAO {

    /**
     * Retourne tous les messages, sans filtrage de canal.
     */
    List<Message> findAll();

    /**
     * Recherche un message par identifiant.
     */
    Message findById(int idMessage);

    /**
     * Retourne les messages d'un canal.
     */
    List<Message> findByCanal(int idCanal);

    /**
     * Cree un message et renseigne son identifiant genere si l'insertion reussit.
     */
    boolean save(Message message);

    /**
     * Met a jour le contenu d'un message.
     */
    boolean update(Message message);

    /**
     * Supprime un message par identifiant.
     */
    boolean delete(int idMessage);
}
