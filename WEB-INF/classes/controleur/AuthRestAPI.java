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

@WebServlet("/auth/*")
public class AuthRestAPI extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO =
            DAOFactory.getInstance().getUtilisateurDAO();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

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
            LoginRequest loginRequest = objectMapper.readValue(
                    req.getInputStream(),
                    LoginRequest.class
            );

            Utilisateur utilisateur =
                    utilisateurDAO.findByPseudo(loginRequest.getPseudo());

            if (utilisateur == null ||
                    !PasswordUtils.verifyPassword(
                            loginRequest.getMotDePasse(),
                            utilisateur.getMotDePasseHash()
                    )) {

                writeJsonResponse(res, HttpServletResponse.SC_UNAUTHORIZED,
                        new APIMessage("Pseudo ou mot de passe incorrect"));
                return;
            }

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
