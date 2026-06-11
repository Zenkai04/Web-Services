package dto;

import java.sql.Timestamp;

/**
 * DTO CANAL - Representation Java d'un canal de discussion.
 *
 * Ce DTO transporte les informations de la table canal entre les DAO,
 * les controleurs REST et la serialisation JSON.
 */
public class Canal {
    private int idCanal;
    private int idAdmin;
    private String nom;
    private String description;
    private String typeCanal;
    private String slug;
    private Timestamp dateCreation;

    /**
     * Constructeur vide requis par Gson et par les mappers JDBC.
     */
    public Canal() {
    }

    /**
     * Constructeur complet utile pour creer un objet canal deja initialise.
     */
    public Canal(int idCanal, int idAdmin, String nom, String description,
            String typeCanal, String slug, Timestamp dateCreation) {
        this.idCanal = idCanal;
        this.idAdmin = idAdmin;
        this.nom = nom;
        this.description = description;
        this.typeCanal = typeCanal;
        this.slug = slug;
        this.dateCreation = dateCreation;
    }

    public int getIdCanal() {
        return idCanal;
    }

    public void setIdCanal(int idCanal) {
        this.idCanal = idCanal;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTypeCanal() {
        return typeCanal;
    }

    public void setTypeCanal(String typeCanal) {
        this.typeCanal = typeCanal;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Timestamp getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Timestamp dateCreation) {
        this.dateCreation = dateCreation;
    }
}
