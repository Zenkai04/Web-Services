package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {

    private PasswordUtils() {
        // Classe utilitaire : pas d'instanciation
    }

    public static String hashPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();

            for (byte b : encodedHash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithme SHA-256 indisponible", e);
        }
    }

    public static boolean verifyPassword(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }

        String rawPasswordHash = hashPassword(rawPassword);
        return rawPasswordHash.equals(storedHash);
    }
}
