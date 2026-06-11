package dto;

/**
 * DTO AUTHENTIFICATION - Reponse envoyee apres une connexion reussie.
 *
 * Contenu :
 * - Le token JWT a utiliser dans le header Authorization.
 * - La duree de validite du token.
 * - Les informations publiques de l'utilisateur connecte.
 */
public class AuthResponse {

    private String token;
    private long expiresIn;
    private UtilisateurPublic utilisateur;

    /**
     * Constructeur vide requis par Gson.
     */
    public AuthResponse() {}

    /**
     * Construit la reponse d'authentification complete.
     */
    public AuthResponse(String token, long expiresIn, UtilisateurPublic utilisateur) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.utilisateur = utilisateur;
    }

    public String getToken() {
        return token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public UtilisateurPublic getUtilisateur() {
        return utilisateur;
    }
}
