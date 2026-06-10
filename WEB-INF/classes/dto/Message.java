package dto;

import java.sql.Timestamp;

public class Message {
    private int idMessage;
    private int idUtilisateur;
    private int idCanal;
    private String contenu;
    private Timestamp dateCreation;
    private Timestamp dateModification;

    public Message(int idMessage, int idUtilisateur, int idCanal, String contenu,
            Timestamp dateCreation, Timestamp dateModification) {
        this.idMessage = idMessage;
        this.idUtilisateur = idUtilisateur;
        this.idCanal = idCanal;
        this.contenu = contenu;
        this.dateCreation = dateCreation;
        this.dateModification = dateModification;
    }

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
