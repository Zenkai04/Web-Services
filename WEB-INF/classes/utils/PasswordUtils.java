package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * CLASSE UTILITAIRE - MOTS DE PASSE
 *
 * Responsabilites :
 * - Hasher les mots de passe avant insertion ou mise a jour en base.
 * - Comparer un mot de passe saisi avec un hash stocke.
 *
 * Securite :
 * - Le mot de passe en clair n'est jamais stocke.
 * - L'algorithme utilise est SHA-256.
 */
public class PasswordUtils {

    private PasswordUtils() {
        // Classe utilitaire : pas d'instanciation
    }

    /**
     * Transforme un mot de passe en hash hexadecimal SHA-256.
     */
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

    /**
     * Compare un mot de passe en clair avec un hash deja stocke en base.
     */
    public static boolean verifyPassword(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }

        String rawPasswordHash = hashPassword(rawPassword);
        return rawPasswordHash.equals(storedHash);
    }
}
