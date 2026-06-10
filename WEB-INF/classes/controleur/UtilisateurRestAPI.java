package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CanalDAO;
import dao.DAOFactory;
import dao.MembreCanalDAO;
import dao.UtilisateurDAO;
import dto.APIMessage;
import dto.Canal;
import dto.Utilisateur;
import dto.UtilisateurPublic;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.PasswordUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/utilisateurs/*")
public class UtilisateurRestAPI extends HttpServlet {

    private final UtilisateurDAO utilisateurDAO = DAOFactory.getInstance().getUtilisateurDAO();
    private final CanalDAO canalDAO = DAOFactory.getInstance().getCanalDAO();
    private final MembreCanalDAO membreCanalDAO = DAOFactory.getInstance().getMembreCanalDAO();
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

        if (isCollectionPath(pathInfo)) {
            handleGetUtilisateurs(res);
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length == 2) {
            handleGetUtilisateur(parts[1], res);
            return;
        }

        if (parts.length == 3 && "canaux".equals(parts[2])) {
            handleGetCanalByUtilisateur(parts[1], res);
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("URI invalide"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String pathInfo = req.getPathInfo();

        if (isCollectionPath(pathInfo)) {
            handleCreateUtilisateur(req, res);
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("URI invalide"));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String pathInfo = req.getPathInfo();

        if (isCollectionPath(pathInfo)) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant d'utilisateur manquant"));
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length == 2) {
            handleUpdateUtilisateur(parts[1], req, res);
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("URI invalide pour la mise a jour d'utilisateur"));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                new APIMessage("La suppression de l'utilisateur n'est pas autorisee"));
    }

    private void handleGetUtilisateurs(HttpServletResponse res) throws IOException {
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();
        writeJsonResponse(res, HttpServletResponse.SC_OK, toPublicList(utilisateurs));
    }

    private void handleGetUtilisateur(String idUtilisateurPart, HttpServletResponse res)
            throws IOException {
        try {
            int idUtilisateur = Integer.parseInt(idUtilisateurPart);
            Utilisateur utilisateur = utilisateurDAO.findById(idUtilisateur);

            if (utilisateur == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Utilisateur introuvable"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK, toPublic(utilisateur));
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant d'utilisateur invalide"));
        }
    }

    private void handleCreateUtilisateur(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        try {
            Utilisateur utilisateur = objectMapper.readValue(req.getInputStream(), Utilisateur.class);
            utilisateur.setMotDePasseHash(
                    PasswordUtils.hashPassword(utilisateur.getMotDePasseHash()));

            boolean created = utilisateurDAO.save(utilisateur);

            if (!created) {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Erreur lors de la creation de l'utilisateur"));
                return;
            }

            addUtilisateurToPublicCanaux(utilisateur.getIdUtilisateur());

            writeJsonResponse(res, HttpServletResponse.SC_CREATED, toPublic(utilisateur));
        } catch (Exception e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("JSON invalide"));
        }
    }

    private void handleUpdateUtilisateur(String idUtilisateurPart, HttpServletRequest req,
            HttpServletResponse res) throws IOException {
        try {
            int idUtilisateur = Integer.parseInt(idUtilisateurPart);
            Utilisateur existingUtilisateur = utilisateurDAO.findById(idUtilisateur);

            if (existingUtilisateur == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Utilisateur introuvable"));
                return;
            }

            Utilisateur updatedUtilisateur = objectMapper.readValue(req.getReader(), Utilisateur.class);
            updatedUtilisateur.setIdUtilisateur(idUtilisateur);
            updatedUtilisateur.setMotDePasseHash(
                    PasswordUtils.hashPassword(updatedUtilisateur.getMotDePasseHash()));

            boolean updated = utilisateurDAO.update(updatedUtilisateur);

            if (!updated) {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Erreur lors de la mise a jour de l'utilisateur"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK, toPublic(updatedUtilisateur));
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant d'utilisateur invalide"));
        } catch (Exception e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("JSON invalide"));
        }
    }

    private void handleGetCanalByUtilisateur(String idUtilisateurPart, HttpServletResponse res)
            throws IOException {
        try {
            int idUtilisateur = Integer.parseInt(idUtilisateurPart);
            Utilisateur utilisateur = utilisateurDAO.findById(idUtilisateur);

            if (utilisateur == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Utilisateur introuvable"));
                return;
            }

            List<Canal> canals = canalDAO.findByUtilisateurId(idUtilisateur);
            writeJsonResponse(res, HttpServletResponse.SC_OK, canals);
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant d'utilisateur invalide"));
        }
    }

    private UtilisateurPublic toPublic(Utilisateur utilisateur) {
        return new UtilisateurPublic(utilisateur);
    }

    private List<UtilisateurPublic> toPublicList(List<Utilisateur> utilisateurs) {
        return utilisateurs.stream()
                .map(this::toPublic)
                .collect(Collectors.toList());
    }

    private void addUtilisateurToPublicCanaux(int idUtilisateur) {
        List<Canal> canaux = canalDAO.findAll();

        for (Canal canal : canaux) {
            if (isPublicCanal(canal)) {
                membreCanalDAO.addMembre(idUtilisateur, canal.getIdCanal());
            }
        }
    }

    private boolean isPublicCanal(Canal canal) {
        return canal != null && "public".equalsIgnoreCase(canal.getTypeCanal());
    }

    private boolean isCollectionPath(String pathInfo) {
        return pathInfo == null || "/".equals(pathInfo);
    }
}
