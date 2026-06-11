package dto;

/**
 * DTO LOGIN - Donnees envoyees par le frontend lors d'une connexion.
 *
 * Attention :
 * - Le mot de passe est recu en clair uniquement dans cette requete.
 * - Il doit ensuite etre compare au hash stocke en base.
 */
public class LoginRequest {

    private String pseudo;
    private String motDePasse;

    /**
     * Constructeur vide requis par Gson.
     */
    public LoginRequest() {}

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}
