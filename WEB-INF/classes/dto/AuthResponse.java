package dto;

public class AuthResponse {

    private String token;
    private long expiresIn;
    private UtilisateurPublic utilisateur;

    public AuthResponse() {}

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