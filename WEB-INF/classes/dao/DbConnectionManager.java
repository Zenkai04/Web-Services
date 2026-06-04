package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * GESTIONNAIRE DE CONNEXIONS - Singleton Pattern
 * 
 * Responsabilités :
 * - Charger les paramètres de connexion depuis db.properties
 * - Établir les connexions à la base de données PostgreSQL
 * - Charger le driver PostgreSQL
 * 
 * Pattern Singleton :
 * - Une seule instance en mémoire
 * - Initialisation lazy (à la première utilisation)
 * - Thread-safe grâce à synchronized
 * 
 * Configuration nécessaire dans db.properties :
 * - db.url : URL de connexion à PostgreSQL
 * - db.user : Nom d'utilisateur PostgreSQL
 * - db.password : Mot de passe PostgreSQL
 * 
 * Exemple de db.properties :
 * db.url=jdbc:postgresql://localhost:5432/projet
 * db.user=postgres
 * db.password=password
 */
public class DbConnectionManager {
    // Instance unique du singleton
    private static DbConnectionManager instance;

    // Paramètres de connexion lus depuis le fichier properties
    private final String url;
    private final String user;
    private final String password;

    /**
     * Constructeur privé - Initialisation du singleton
     * 
     * Procédure :
     * 1. Charge le fichier db.properties
     * 2. Charge le driver PostgreSQL
     * 3. Extrait les paramètres de connexion
     * 
     * @throws IllegalStateException Si le fichier n'existe pas ou les paramètres manquent
     */
    private DbConnectionManager() {
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            // Vérification que le fichier existe
            if (input == null) {
                throw new IllegalStateException("Fichier db.properties introuvable");
            }

            // Chargement des propriétés depuis le fichier
            Properties props = new Properties();
            props.load(input);
            
            // Chargement du driver PostgreSQL
            Class.forName("org.postgresql.Driver");

            // Extraction des paramètres de connexion
            this.url = props.getProperty("db.url");
            this.user = props.getProperty("db.user");
            this.password = props.getProperty("db.password");
        } catch (Exception e) {
            throw new IllegalStateException("Impossible d'initialiser la connexion a la base", e);
        }
    }

    /**
     * Méthode pour obtenir l'instance unique du singleton
     * Initialisation lazy : l'instance est créée à la première utilisation
     * 
     * @return L'instance unique de DbConnectionManager
     */
    public static synchronized DbConnectionManager getInstance() {
        if (instance == null) {
            instance = new DbConnectionManager();
        }
        return instance;
    }

    /**
     * Méthode pour obtenir une nouvelle connexion à la base de données
     * À appeler à chaque fois qu'une connexion est nécessaire
     * 
     * @return Une nouvelle connexion JDBC à PostgreSQL
     * @throws SQLException Si la connexion échoue
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
