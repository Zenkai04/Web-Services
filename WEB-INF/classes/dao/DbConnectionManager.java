package dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * GESTIONNAIRE DE CONNEXION - Configuration JDBC de l'application.
 *
 * Responsabilites :
 * - Charger le driver PostgreSQL.
 * - Lire les informations de connexion depuis db.properties.
 * - Fournir une connexion JDBC neuve aux DAO.
 */
public class DbConnectionManager {
    private static DbConnectionManager instance;

    private final String url;
    private final String user;
    private final String password;

    /**
     * Initialise les parametres de connexion au demarrage de la premiere DAO.
     */
    private DbConnectionManager() {
        try {
            Properties props = loadProperties();
            Class.forName("org.postgresql.Driver");

            this.url = getRequiredProperty(props, "DB_URL");
            this.user = getRequiredProperty(props, "DB_USER");
            this.password = getRequiredProperty(props, "DB_PASSWORD");
        } catch (Exception e) {
            throw new IllegalStateException("Impossible d'initialiser la connexion a la base", e);
        }
    }

    /**
     * Retourne l'instance unique du gestionnaire de connexion.
     */
    public static synchronized DbConnectionManager getInstance() {
        if (instance == null) {
            instance = new DbConnectionManager();
        }
        return instance;
    }

    /**
     * Ouvre une connexion JDBC vers la base configuree.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Charge db.properties depuis le classpath ou depuis les emplacements fichiers prevus.
     */
    private Properties loadProperties() throws IOException {
        Properties props = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                props.load(input);
                return props;
            }
        }

        for (Path candidate : getFileCandidates()) {
            if (Files.exists(candidate)) {
                try (InputStream input = Files.newInputStream(candidate)) {
                    props.load(input);
                    return props;
                }
            }
        }

        throw new IllegalStateException("Fichier db.properties introuvable");
    }

    /**
     * Liste les emplacements possibles du fichier de configuration.
     */
    private Path[] getFileCandidates() {
        Path classesDir = getClassesDir();

        return new Path[] {
            classesDir.resolve("db.properties"),
            classesDir.resolve("..").resolve("..").resolve("db.properties").normalize(),
            Paths.get("db.properties")
        };
    }

    /**
     * Retrouve le dossier contenant les classes compilees de l'application.
     */
    private Path getClassesDir() {
        try {
            return Paths.get(getClass()
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Chemin des classes invalide", e);
        }
    }

    /**
     * Lit une propriete obligatoire et retire les guillemets eventuels.
     */
    private String getRequiredProperty(Properties props, String key) {
        String value = props.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Propriete manquante : " + key);
        }

        return value.trim().replaceAll("^\"|\"$", "");
    }
}
