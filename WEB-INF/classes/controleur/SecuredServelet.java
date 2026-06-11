package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.APIMessage;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import utils.JwtManager;

/**
 * SERVLET ABSTRAITE SECURISEE - Base commune des API protegees.
 *
 * Responsabilites :
 * - Centraliser la verification du header Authorization: Bearer.
 * - Refuser les requetes sans token ou avec un token invalide.
 * - Fournir l'identifiant de l'utilisateur connecte aux controleurs enfants.
 * - Produire des reponses JSON coherentes pour les erreurs 401 et 403.
 *
 * Utilisation :
 * - Les controleurs REST proteges heritent de cette classe.
 * - AuthRestAPI n'en herite pas, car le login doit rester public.
 */
public abstract class SecuredServelet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Verifie que la requete contient un token JWT valide.
     *
     * @return true si la requete est authentifiee, false sinon.
     */
    protected boolean checkAuthentication(
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException {

        String token = JwtManager.extractTokenFromRequest(req);

        if (token == null || !JwtManager.isValidToken(token)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().print(objectMapper.writeValueAsString(
                    new APIMessage("Token manquant ou invalide")));
            return false;
        }

        return true;
    }

    /**
     * Extrait l'identifiant utilisateur depuis le token deja valide.
     * Cette methode est appelee apres checkAuthentication().
     */
    protected int getAuthenticatedUserId(HttpServletRequest req) {
        String token = JwtManager.extractTokenFromRequest(req);
        return JwtManager.extractUserId(token);
    }

    /**
     * Reponse standard quand l'utilisateur est authentifie mais n'a pas le droit
     * d'effectuer l'action demandee.
     */
    protected void writeForbidden(HttpServletResponse res)
            throws IOException {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().print(objectMapper.writeValueAsString(
                new APIMessage("Acces refuse")));
    }
}
