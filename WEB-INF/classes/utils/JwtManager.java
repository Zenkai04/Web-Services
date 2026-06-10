package util;

import dto.Utilisateur;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import java.util.Date;

public class JwtManager {

    private static final String SECRET =
            "cle-secrete-tres-longue-pour-signer-les-jwt-du-projet";

    private static final long EXPIRATION_MS = 15 * 60 * 1000; // 15 minutes

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private JwtManager() {
        
    }

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

    private static Claims parseToken(String token) {
    return Jwts.parser()
            .verifyWith(KEY)
            .build()
            .parseSignedClaims(token)
            .getPayload();
}
}