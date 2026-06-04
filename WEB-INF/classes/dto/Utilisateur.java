package dto;

import java.sql.Timestamp;

public class Utilisateur {
    private int idUtilisateur;
    private String pseudo;
    private String email;
    private String motDePasseHash;
    private Timestamp dateInscription;

    public Utilisateur(int idUtilisateur, String pseudo, String email, String motDePasseHash, Timestamp dateInscription) {
        this.idUtilisateur = idUtilisateur;
        this.pseudo = pseudo;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.dateInscription = dateInscription;
    }

    public Utilisateur() {
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    public Timestamp getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Timestamp dateInscription) {
        this.dateInscription = dateInscription;
    }
}
