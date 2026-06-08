package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DAOFactory;
import dao.UtilisateurDAO;
import dto.APIMessage;
import dto.Utilisateur;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/utilisateurs/*")
public class UtilisateurRestAPI extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO = DAOFactory.getInstance().getUtilisateurDAO();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private void writeJsonResponse(HttpServletResponse res, int status, Object data)
            throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().print(objectMapper.writeValueAsString(data));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        String pathInfo = req.getPathInfo();

        // GET /utilisateurs
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
            writeJsonResponse(res, HttpServletResponse.SC_OK, utilisateurs);
            return;
        }

        String[] parts = pathInfo.split("/");

        // GET /utilisateurs/{id}
        if (parts.length == 2) {
            handleGetUtilisateur(parts[1], res);
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("URI invalide"));
    }

    private void handleGetUtilisateur(String idStr, HttpServletResponse res)
            throws IOException {
        try {
            int idUtilisateur = Integer.parseInt(idStr);

            Utilisateur utilisateur = utilisateurDAO.findById(idUtilisateur);

            if (utilisateur == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Utilisateur introuvable"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK, utilisateur);

        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant d'utilisateur invalide"));
        }
    }
}