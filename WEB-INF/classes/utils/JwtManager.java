package utils;

import dto.Utilisateur;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

/**
 * CLASSE UTILITAIRE - JWT
 *
 * Responsabilites :
 * - Generer les tokens JWT renvoyes apres authentification.
 * - Verifier la signature et l'expiration des tokens recus.
 * - Extraire les informations utiles du token : id utilisateur et pseudo.
 * - Lire le header HTTP Authorization au format Bearer.
 *
 * Securite :
 * - La cle HMAC est derivee depuis une chaine secrete suffisamment longue.
 * - Le token expire apres 15 minutes.
 * - La deconnexion est geree cote frontend par suppression du token de session.
 */
public class JwtManager {

    private static final String SECRET =
            "cle-secrete-tres-longue-pour-signer-les-jwt-du-projet";

    private static final long EXPIRATION_MS = 15 * 60 * 1000; // 15 minutes

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private JwtManager() {
        // Classe utilitaire : pas d'instanciation
    }

    /**
     * Genere un token pour un utilisateur authentifie.
     * Le subject du token contient l'id utilisateur.
     */
    public static String generateToken(Utilisateur utilisateur) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiration = new Date(nowMillis + EXPIRATION_MS);

        return Jwts.builder()
                .subject(String.valueOf(utilisateur.getIdUtilisateur()))
                .claim("pseudo", utilisateur.getPseudo())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(KEY)
                .compact();
    }

    /**
     * Parse et verifie un token signe.
     * Une exception est levee si le token est invalide ou expire.
     */
    private static Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Indique si un token est utilisable pour acceder aux routes protegees.
     */
    public static boolean isValidToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrait l'identifiant utilisateur stocke dans le subject du token.
     */
    public static int extractUserId(String token) {
        Claims claims = parseToken(token);
        return Integer.parseInt(claims.getSubject());
    }

    /**
     * Extrait le pseudo stocke comme claim informatif.
     */
    public static String extractPseudo(String token) {
        Claims claims = parseToken(token);
        return claims.get("pseudo", String.class);
    }

    /**
     * Lit le token depuis le header Authorization: Bearer <token>.
     */
    public static String extractTokenFromRequest(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring("Bearer ".length()).trim();
    }


}
