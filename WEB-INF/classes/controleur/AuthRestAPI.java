package controleur;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DAOFactory;
import dao.UtilisateurDAO;
import dto.APIMessage;
import dto.AuthResponse;
import dto.LoginRequest;
import dto.Utilisateur;
import dto.UtilisateurPublic;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.JwtManager;
import utils.PasswordUtils;

/**
 * CONTROLEUR REST - AUTHENTIFICATION
 *
 * Responsabilites :
 * - Exposer l'endpoint public POST /auth/login.
 * - Verifier le pseudo et le mot de passe recus en JSON.
 * - Generer un JWT signe lorsque les identifiants sont valides.
 * - Retourner une reponse d'authentification sans exposer le hash du mot de passe.
 *
 * Securite :
 * - Cette servlet reste volontairement publique, car elle sert a obtenir un token.
 * - La comparaison des mots de passe passe par PasswordUtils.
 * - Les autres methodes HTTP sont refusees explicitement.
 */
@WebServlet("/auth/*")
public class AuthRestAPI extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO =
            DAOFactory.getInstance().getUtilisateurDAO();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    /**
     * Ecrit une reponse JSON uniforme pour toutes les routes de cette servlet.
     */
    private void writeJsonResponse(HttpServletResponse res, int status, Object data)
            throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().print(objectMapper.writeValueAsString(data));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                new APIMessage("La récupération d'un token doit se faire via POST"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        // POST /auth/login : seule route de creation de token exposee par l'API.
        String pathInfo = req.getPathInfo();
        if ("/login".equals(pathInfo)) {
            handleLogin(req, res);
            return;
        }
        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("URI invalide"));
        }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                new APIMessage("La mise à jour d'un token n'est pas supportée"));   
        }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                new APIMessage("La suppression d'un token n'est pas supportée"));
        }

    private void handleLogin(HttpServletRequest req, HttpServletResponse res)
        throws IOException {
        try {
            // Deserialisation du corps JSON : { "pseudo": "...", "motDePasse": "..." }
            LoginRequest loginRequest = objectMapper.readValue(
                    req.getInputStream(),
                    LoginRequest.class
            );

            Utilisateur utilisateur =
                    utilisateurDAO.findByPseudo(loginRequest.getPseudo());

            // Refus si le pseudo est inconnu ou si le hash du mot de passe ne correspond pas.
            if (utilisateur == null ||
                    !PasswordUtils.verifyPassword(
                            loginRequest.getMotDePasse(),
                            utilisateur.getMotDePasseHash()
                    )) {

                writeJsonResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
                        new APIMessage("Pseudo ou mot de passe incorrect"));
                return;
            }

            // Generation du JWT contenant l'id utilisateur en subject et le pseudo en claim.
            String token = JwtManager.generateToken(utilisateur);

            AuthResponse authResponse = new AuthResponse(
                    token,
                    15 * 60,
                    toPublic(utilisateur)
            );

            writeJsonResponse(res, HttpServletResponse.SC_OK, authResponse);

        } catch (Exception e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("JSON invalide"));
        }
    }

    private UtilisateurPublic toPublic(Utilisateur utilisateur) {
        return new UtilisateurPublic(utilisateur);
    }
}
