package dto;

import java.sql.Timestamp;

/**
 * DTO MESSAGE - Representation Java d'un message de canal.
 *
 * Chaque message est obligatoirement rattache a un utilisateur auteur
 * et a un canal. Les controleurs appliquent les regles d'acces avant
 * d'exposer ou de modifier ces donnees.
 */
public class Message {
    private int idMessage;
    private int idUtilisateur;
    private int idCanal;
    private String contenu;
    private Timestamp dateCreation;
    private Timestamp dateModification;

    /**
     * Constructeur complet utile pour creer un message deja initialise.
     */
    public Message(int idMessage, int idUtilisateur, int idCanal, String contenu,
            Timestamp dateCreation, Timestamp dateModification) {
        this.idMessage = idMessage;
        this.idUtilisateur = idUtilisateur;
        this.idCanal = idCanal;
        this.contenu = contenu;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

    /**
     * Constructeur vide requis par Gson et par les mappers JDBC.
     */
    public Message() {
    }

    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public int getIdCanal() {
        return idCanal;
    }

    public void setIdCanal(int idCanal) {
        this.idCanal = idCanal;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Timestamp getDateModification() {
        return dateModification;
    }

    public void setDateModification(Timestamp dateModification) {
        this.dateModification = dateModification;
    }
}
