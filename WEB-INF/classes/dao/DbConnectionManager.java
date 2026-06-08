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

public class DbConnectionManager {
    private static DbConnectionManager instance;

    private final String url;
    private final String user;
    private final String password;

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

    public static synchronized DbConnectionManager getInstance() {
        if (instance == null) {
            instance = new DbConnectionManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

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

    private Path[] getFileCandidates() {
        Path classesDir = getClassesDir();

        return new Path[] {
            classesDir.resolve("db.properties"),
            classesDir.resolve("..").resolve("..").resolve("db.properties").normalize(),
            Paths.get("db.properties")
        };
    }

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

    private String getRequiredProperty(Properties props, String key) {
        String value = props.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Propriete manquante : " + key);
        }

        return value.trim().replaceAll("^\"|\"$", "");
    }
}
