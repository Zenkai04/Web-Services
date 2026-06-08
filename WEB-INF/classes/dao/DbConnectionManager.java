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

            this.url = getRequiredProperty(props, "db.url", "DB_URL");
            this.user = getRequiredProperty(props, "db.user", "DB_USER");
            this.password = getRequiredProperty(props, "db.password", "DB_PASSWORD");
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

    private String getRequiredProperty(Properties props, String lowerKey, String upperKey) {
        String value = props.getProperty(lowerKey);

        if (value == null || value.isBlank()) {
            value = props.getProperty(upperKey);
        }

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Propriete manquante : " + lowerKey + " ou " + upperKey);
        }

        return value.trim().replaceAll("^\"|\"$", "");
    }
}
