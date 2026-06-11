package dto;

/**
 * DTO REPONSE SIMPLE - Message textuel renvoye par l'API.
 *
 * Utilisation :
 * - Confirmer une action reussie.
 * - Expliquer une erreur fonctionnelle ou de securite.
 * - Garder un format JSON homogene pour les reponses courtes.
 */
public class APIMessage {

    private String message;

    /**
     * Constructeur vide requis par Gson lors de la deserialisation.
     */
    public APIMessage() {
    }

    /**
     * Cree une reponse contenant uniquement un message.
     */
    public APIMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
