package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CanalDAO;
import dao.DAOFactory;
import dao.MembreCanalDAO;
import dao.MessageDAO;
import dao.UtilisateurDAO;
import dto.APIMessage;
import dto.Canal;
import dto.Message;
import dto.Utilisateur;
import dto.UtilisateurPublic;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CONTROLEUR REST - CANAUX
 *
 * Responsabilites :
 * - Exposer les routes REST de gestion des canaux.
 * - Gerer les messages et les membres dans le contexte d'un canal.
 * - Appliquer les regles metier : canal public, appartenance, auteur, administrateur.
 *
 * Securite :
 * - Toutes les routes exigent un JWT valide.
 * - Les canaux publics sont visibles par tous les utilisateurs connectes.
 * - Les canaux prives ne sont visibles que par leurs membres.
 * - Seul l'administrateur d'un canal peut le modifier ou ajouter des membres.
 */
@WebServlet("/canaux/*")
public class CanalRestAPI extends SecuredServelet {

    private final CanalDAO canalDAO = DAOFactory.getInstance().getCanalDAO();
    private final MessageDAO messageDAO = DAOFactory.getInstance().getMessageDAO();
    private final MembreCanalDAO membreCanalDAO = DAOFactory.getInstance().getMembreCanalDAO();
    private final UtilisateurDAO utilisateurDAO = DAOFactory.getInstance().getUtilisateurDAO();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Ecrit une reponse JSON avec le code HTTP fourni.
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
        // Toutes les lectures de canaux necessitent un token valide.
        if (!checkAuthentication(req, res)) {
            return;
        }

        int idUtilisateurConnecte = getAuthenticatedUserId(req);
        String pathInfo = req.getPathInfo();

        // GET /canaux
        if (isCollectionPath(pathInfo)) {
            handleGetCanaux(idUtilisateurConnecte, res);
            return;
        }

        String[] parts = pathInfo.split("/");

        // GET /canaux/{id}
        if (parts.length == 2) {
            handleGetCanal(parts[1], idUtilisateurConnecte, res);
            return;
        }

        // GET /canaux/{id}/messages
        if (parts.length == 3 && "messages".equals(parts[2])) {
            handleGetCanalMessages(parts[1], idUtilisateurConnecte, res);
            return;
        }

        // GET /canaux/{id}/membres
        if (parts.length == 3 && "membres".equals(parts[2])) {
            handleGetCanalMembres(parts[1], idUtilisateurConnecte, res);
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("URI invalide"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        // Creation de canal, message ou membre : token obligatoire.
        if (!checkAuthentication(req, res)) {
            return;
        }

        int idUtilisateurConnecte = getAuthenticatedUserId(req);
        String pathInfo = req.getPathInfo();

        // POST /canaux
        if (isCollectionPath(pathInfo)) {
            handleCreateCanal(idUtilisateurConnecte, req, res);
            return;
        }

        String[] parts = pathInfo.split("/");

        // POST /canaux/{id}/messages
        if (parts.length == 3 && "messages".equals(parts[2])) {
            handleCreateMessageInCanal(parts[1], idUtilisateurConnecte, req, res);
            return;
        }

        // POST /canaux/{id}/membres
        if (parts.length == 3 && "membres".equals(parts[2])) {
            handleAddMembreToCanal(parts[1], idUtilisateurConnecte, req, res);
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("URI invalide pour la creation"));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        // PUT est limite a la modification d'un canal par son administrateur.
        if (!checkAuthentication(req, res)) {
            return;
        }

        int idUtilisateurConnecte = getAuthenticatedUserId(req);
        String pathInfo = req.getPathInfo();

        if (isCollectionPath(pathInfo)) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("URI invalide pour la mise a jour d'un canal"));
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length == 2) {
            handleUpdateCanal(parts[1], idUtilisateurConnecte, req, res);
            return;
        }

        if (parts.length >= 3 && "messages".equals(parts[2])) {
            writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    new APIMessage("Mise a jour de messages via cette URI non autorisee"));
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("URI invalide pour la mise a jour d'un canal"));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        // Suppressions autorisees uniquement sur les sous-ressources messages/membres.
        if (!checkAuthentication(req, res)) {
            return;
        }

        int idUtilisateurConnecte = getAuthenticatedUserId(req);
        String pathInfo = req.getPathInfo();

        if (isCollectionPath(pathInfo)) {
            writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    new APIMessage("La suppression de canal n'est pas autorisee"));
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length == 2) {
            writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    new APIMessage("La suppression de canal n'est pas autorisee"));
            return;
        }

        if (parts.length == 3 && "messages".equals(parts[2])) {
            writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    new APIMessage("La suppression de messages via cette URI n'est pas autorisee"));
            return;
        }

        if (parts.length == 4 && "messages".equals(parts[2])) {
            handleDeleteMessageFromCanal(parts[1], parts[3], idUtilisateurConnecte, res);
            return;
        }

        if (parts.length == 3 && "membres".equals(parts[2])) {
            writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    new APIMessage("La suppression de membres via cette URI n'est pas autorisee"));
            return;
        }

        if (parts.length == 4 && "membres".equals(parts[2])) {
            handleRemoveMembreFromCanal(parts[1], parts[3], idUtilisateurConnecte, res);
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("URI invalide pour la suppression"));
    }

    private void handleGetCanaux(int idUtilisateurConnecte, HttpServletResponse res)
            throws IOException {
        // Filtrage serveur : l'utilisateur ne voit que les canaux accessibles.
        List<Canal> canaux = canalDAO.findAll().stream()
                .filter(canal -> canAccessCanal(idUtilisateurConnecte, canal))
                .collect(Collectors.toList());
        writeJsonResponse(res, HttpServletResponse.SC_OK, canaux);
    }

    private void handleGetCanal(String idCanalPart, int idUtilisateurConnecte,
            HttpServletResponse res) throws IOException {
        try {
            int idCanal = Integer.parseInt(idCanalPart);
            Canal canal = canalDAO.findById(idCanal);

            if (canal == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Canal introuvable"));
                return;
            }

            if (!canAccessCanal(idUtilisateurConnecte, canal)) {
                writeForbidden(res);
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK, canal);
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de canal invalide"));
        }
    }

    private void handleGetCanalMessages(String idCanalPart, int idUtilisateurConnecte,
            HttpServletResponse res) throws IOException {
        try {
            int idCanal = Integer.parseInt(idCanalPart);
            Canal canal = canalDAO.findById(idCanal);

            if (canal == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Canal introuvable"));
                return;
            }

            if (!canAccessCanal(idUtilisateurConnecte, canal)) {
                writeForbidden(res);
                return;
            }

            List<Message> messages = messageDAO.findByCanal(idCanal);
            writeJsonResponse(res, HttpServletResponse.SC_OK, messages);
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de canal invalide"));
        }
    }

    private void handleGetCanalMembres(String idCanalPart, int idUtilisateurConnecte,
            HttpServletResponse res) throws IOException {
        try {
            int idCanal = Integer.parseInt(idCanalPart);
            Canal canal = canalDAO.findById(idCanal);

            if (canal == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Canal introuvable"));
                return;
            }

            if (!canAccessCanal(idUtilisateurConnecte, canal)) {
                writeForbidden(res);
                return;
            }

            List<Utilisateur> membres = membreCanalDAO.findByCanal(idCanal);
            writeJsonResponse(res, HttpServletResponse.SC_OK, toPublicList(membres));
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de canal invalide"));
        }
    }

    private void handleCreateCanal(int idUtilisateurConnecte, HttpServletRequest req,
            HttpServletResponse res) throws IOException {
        try {
            Canal canal = objectMapper.readValue(req.getReader(), Canal.class);
            // L'administrateur du canal est impose par le token, pas par le JSON client.
            canal.setIdAdmin(idUtilisateurConnecte);
            boolean created = canalDAO.save(canal);

            if (!created) {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Creation du canal impossible"));
                return;
            }

            if (isPublicCanal(canal)) {
                // Un canal public contient automatiquement tous les utilisateurs.
                addAllUtilisateursToCanal(canal.getIdCanal());
            } else {
                // Pour un canal prive, le createur est au moins membre de son canal.
                membreCanalDAO.addMembre(idUtilisateurConnecte, canal.getIdCanal());
            }

            writeJsonResponse(res, HttpServletResponse.SC_CREATED, canal);
        } catch (Exception e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("JSON invalide"));
        }
    }

    private void handleCreateMessageInCanal(String idCanalPart, int idUtilisateurConnecte,
            HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            int idCanal = Integer.parseInt(idCanalPart);
            Canal canal = canalDAO.findById(idCanal);

            if (canal == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Canal introuvable"));
                return;
            }

            // Seuls les membres peuvent ecrire dans un canal.
            if (!membreCanalDAO.isMembre(idUtilisateurConnecte, idCanal)) {
                writeForbidden(res);
                return;
            }

            Message message = objectMapper.readValue(req.getReader(), Message.class);
            message.setIdCanal(idCanal);
            // L'auteur est impose par le token pour eviter l'usurpation d'identite.
            message.setIdUtilisateur(idUtilisateurConnecte);

            boolean created = messageDAO.save(message);

            if (!created) {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Creation du message impossible"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_CREATED, message);
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de canal invalide"));
        } catch (Exception e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("JSON invalide"));
        }
    }

    private void handleAddMembreToCanal(String idCanalPart, int idUtilisateurConnecte,
            HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            int idCanal = Integer.parseInt(idCanalPart);
            Canal canal = canalDAO.findById(idCanal);

            if (canal == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Canal introuvable"));
                return;
            }

            // Ajout manuel de membre reserve a l'administrateur du canal.
            if (!isCanalAdmin(idUtilisateurConnecte, canal)) {
                writeForbidden(res);
                return;
            }

            Utilisateur utilisateur = objectMapper.readValue(req.getReader(), Utilisateur.class);
            boolean added = membreCanalDAO.addMembre(utilisateur.getIdUtilisateur(), idCanal);

            if (!added) {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Ajout du membre impossible"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_CREATED,
                    new APIMessage("Membre ajoute au canal"));
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de canal invalide"));
        } catch (Exception e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("JSON invalide"));
        }
    }

    private void handleUpdateCanal(String idCanalPart, int idUtilisateurConnecte,
            HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            int idCanal = Integer.parseInt(idCanalPart);
            Canal existingCanal = canalDAO.findById(idCanal);

            if (existingCanal == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Canal introuvable"));
                return;
            }

            // Modification reservee a l'administrateur du canal.
            if (!isCanalAdmin(idUtilisateurConnecte, existingCanal)) {
                writeForbidden(res);
                return;
            }

            Canal updatedCanal = objectMapper.readValue(req.getReader(), Canal.class);
            updatedCanal.setIdCanal(idCanal);
            // L'idAdmin existant est conserve pour eviter un transfert d'administration non controle.
            updatedCanal.setIdAdmin(existingCanal.getIdAdmin());

            boolean updated = canalDAO.update(updatedCanal);

            if (!updated) {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Mise a jour du canal impossible"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK, updatedCanal);
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de canal invalide"));
        } catch (Exception e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("JSON invalide"));
        }
    }

    private void handleDeleteMessageFromCanal(String idCanalPart, String idMessagePart,
            int idUtilisateurConnecte, HttpServletResponse res) throws IOException {
        try {
            int idCanal = Integer.parseInt(idCanalPart);
            int idMessage = Integer.parseInt(idMessagePart);

            Canal canal = canalDAO.findById(idCanal);

            if (canal == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Canal introuvable"));
                return;
            }

            Message message = messageDAO.findById(idMessage);

            if (message == null || message.getIdCanal() != idCanal) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Message introuvable dans ce canal"));
                return;
            }

            // Suppression autorisee pour l'auteur du message ou l'admin du canal.
            if (!isMessageAuthor(idUtilisateurConnecte, message)
                    && !isCanalAdmin(idUtilisateurConnecte, canal)) {
                writeForbidden(res);
                return;
            }

            boolean deleted = messageDAO.delete(idMessage);

            if (!deleted) {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Suppression du message impossible"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK,
                    new APIMessage("Message supprime du canal"));
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de canal ou de message invalide"));
        }
    }

    private void handleRemoveMembreFromCanal(String idCanalPart, String idUtilisateurPart,
            int idUtilisateurConnecte, HttpServletResponse res) throws IOException {
        try {
            int idCanal = Integer.parseInt(idCanalPart);
            int idUtilisateur = Integer.parseInt(idUtilisateurPart);

            Canal canal = canalDAO.findById(idCanal);

            if (canal == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Canal introuvable"));
                return;
            }

            // Un admin peut retirer n'importe quel membre ; un utilisateur peut se retirer lui-meme.
            if (!isCanalAdmin(idUtilisateurConnecte, canal)
                    && idUtilisateurConnecte != idUtilisateur) {
                writeForbidden(res);
                return;
            }

            boolean removed = membreCanalDAO.removeMembre(idUtilisateur, idCanal);

            if (!removed) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Membre introuvable dans ce canal"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK,
                    new APIMessage("Membre supprime du canal"));
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de canal ou d'utilisateur invalide"));
        }
    }

    private List<UtilisateurPublic> toPublicList(List<Utilisateur> utilisateurs) {
        return utilisateurs.stream()
                .map(UtilisateurPublic::new)
                .collect(Collectors.toList());
    }

    private void addAllUtilisateursToCanal(int idCanal) {
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll();

        for (Utilisateur utilisateur : utilisateurs) {
            membreCanalDAO.addMembre(utilisateur.getIdUtilisateur(), idCanal);
        }
    }

    private boolean isPublicCanal(Canal canal) {
        return canal != null && "public".equalsIgnoreCase(canal.getTypeCanal());
    }

    private boolean canAccessCanal(int idUtilisateur, Canal canal) {
        return isPublicCanal(canal)
                || membreCanalDAO.isMembre(idUtilisateur, canal.getIdCanal());
    }

    private boolean isCanalAdmin(int idUtilisateur, Canal canal) {
        return canal != null && canal.getIdAdmin() == idUtilisateur;
    }

    private boolean isMessageAuthor(int idUtilisateur, Message message) {
        return message != null && message.getIdUtilisateur() == idUtilisateur;
    }

    private boolean isCollectionPath(String pathInfo) {
        return pathInfo == null || "/".equals(pathInfo);
    }
}
