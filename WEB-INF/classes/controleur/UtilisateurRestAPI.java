package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DAOFactory;
import dao.UtilisateurDAO;
import dto.APIMessage;
import dto.Utilisateur;
import utils.PasswordUtils;
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            try {
                Utilisateur utilisateur = objectMapper.readValue(req.getInputStream(), Utilisateur.class);

                utilisateur.setMotDePasseHash(
                        PasswordUtils.hashPassword(utilisateur.getMotDePasseHash()));
                
                boolean success = utilisateurDAO.save(utilisateur);

                if (success) {
                    writeJsonResponse(res, HttpServletResponse.SC_CREATED, utilisateur);
                } else {
                    writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                            new APIMessage("Erreur lors de la création de l'utilisateur"));
                }
            } catch (IOException e) {
                writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                        new APIMessage("JSON invalide"));
            }
        } else {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("URI invalide"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant d'utilisateur manquant"));
            return;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length != 2) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("URI invalide pour la mise à jour d'utilisateur"));
            return;
        }
        try {
            int idUtilisateur = Integer.parseInt(parts[1]);

            Utilisateur existingUtilisateur = utilisateurDAO.findById(idUtilisateur);

            if (existingUtilisateur == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Utilisateur introuvable"));
                return;
            }

            Utilisateur updatedUtilisateur = objectMapper.readValue(req.getReader(), Utilisateur.class);
            updatedUtilisateur.setIdUtilisateur(idUtilisateur);

            boolean updated = utilisateurDAO.update(updatedUtilisateur);

            if (updated) {
                writeJsonResponse(res, HttpServletResponse.SC_OK, updatedUtilisateur);
            } else {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Erreur lors de la mise à jour de l'utilisateur"));
            }

        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant d'utilisateur invalide"));
        } catch (IOException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("JSON invalide"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant d'utilisateur manquant"));
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length != 2) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("URI invalide pour la suppression d'utilisateur"));
            return;
        }
        try {
            int idUtilisateur = Integer.parseInt(parts[1]);

            boolean deleted = utilisateurDAO.delete(idUtilisateur);

            if (deleted) {
                writeJsonResponse(res, HttpServletResponse.SC_NO_CONTENT, null);
            } else {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Utilisateur introuvable"));
            }

        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant d'utilisateur invalide"));
        }
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
