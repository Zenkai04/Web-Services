package dto;

import java.sql.Timestamp;

public class UtilisateurPublic {

    private int idUtilisateur;
    private String pseudo;
    private String email;
    private Timestamp dateCreation;

    public UtilisateurPublic() {}

    public UtilisateurPublic(Utilisateur utilisateur) {
        this.idUtilisateur = utilisateur.getIdUtilisateur();
        this.pseudo = utilisateur.getPseudo();
        this.email = utilisateur.getEmail();
        this.dateCreation = utilisateur.getDateCreation();
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

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }
}