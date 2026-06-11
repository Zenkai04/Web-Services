package dto;

import java.sql.Timestamp;

/**
 * DTO UTILISATEUR - Representation complete d'un compte utilisateur.
 *
 * Attention :
 * - Ce DTO contient le hash du mot de passe.
 * - Il est utilise cote DAO et authentification.
 * - Les reponses publiques doivent utiliser UtilisateurPublic.
 */
public class Utilisateur {
    private int idUtilisateur;
    private String pseudo;
    private String email;
    private String motDePasseHash;
    private Timestamp dateCreation;

    /**
     * Constructeur complet utile pour creer un utilisateur deja initialise.
     */
    public Utilisateur(int idUtilisateur, String pseudo, String email,
            String motDePasseHash, Timestamp dateCreation) {
        this.idUtilisateur = idUtilisateur;
        this.pseudo = pseudo;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.dateCreation = dateCreation;
    }

    /**
     * Constructeur vide requis par Gson et par les mappers JDBC.
     */
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

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }
}
