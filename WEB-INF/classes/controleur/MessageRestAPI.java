package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CanalDAO;
import dao.DAOFactory;
import dao.MembreCanalDAO;
import dao.MessageDAO;
import dto.APIMessage;
import dto.Canal;
import dto.Message;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/messages/*")
public class MessageRestAPI extends SecuredServelet {

    private final CanalDAO canalDAO = DAOFactory.getInstance().getCanalDAO();
    private final MessageDAO messageDAO = DAOFactory.getInstance().getMessageDAO();
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
        if (!checkAuthentication(req, res)) {
            return;
        }

        int idUtilisateurConnecte = getAuthenticatedUserId(req);
        String pathInfo = req.getPathInfo();

        if (isCollectionPath(pathInfo)) {
            handleGetMessages(idUtilisateurConnecte, res);
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length == 2) {
            handleGetMessage(parts[1], idUtilisateurConnecte, res);
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("Chemin invalide"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        if (!checkAuthentication(req, res)) {
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                new APIMessage("La creation d'un message doit se faire via son canal"));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        if (!checkAuthentication(req, res)) {
            return;
        }

        int idUtilisateurConnecte = getAuthenticatedUserId(req);
        String pathInfo = req.getPathInfo();

        if (isCollectionPath(pathInfo)) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de message manquant"));
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length == 2) {
            handleUpdateMessage(parts[1], idUtilisateurConnecte, req, res);
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("URI invalide pour la mise a jour de message"));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        if (!checkAuthentication(req, res)) {
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                new APIMessage("La suppression d'un message doit se faire via son canal"));
    }

    private void handleGetMessages(int idUtilisateurConnecte, HttpServletResponse res)
            throws IOException {
        List<Message> messages = messageDAO.findAll().stream()
                .filter(message -> canAccessMessage(idUtilisateurConnecte, message))
                .toList();
        writeJsonResponse(res, HttpServletResponse.SC_OK, messages);
    }

    private void handleGetMessage(String idMessagePart, int idUtilisateurConnecte,
            HttpServletResponse res) throws IOException {
        try {
            int idMessage = Integer.parseInt(idMessagePart);
            Message message = messageDAO.findById(idMessage);

            if (message == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Message introuvable"));
                return;
            }

            if (!canAccessMessage(idUtilisateurConnecte, message)) {
                writeForbidden(res);
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK, message);
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("ID de message invalide"));
        }
    }

    private void handleUpdateMessage(String idMessagePart, int idUtilisateurConnecte,
            HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            int idMessage = Integer.parseInt(idMessagePart);

            Message existingMessage = messageDAO.findById(idMessage);

            if (existingMessage == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Message introuvable"));
                return;
            }

            if (existingMessage.getIdUtilisateur() != idUtilisateurConnecte) {
                writeForbidden(res);
                return;
            }

            Message updatedMessage = objectMapper.readValue(req.getReader(), Message.class);
            updatedMessage.setIdMessage(idMessage);

            boolean updated = messageDAO.update(updatedMessage);

            if (!updated) {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Mise a jour du message impossible"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK, updatedMessage);
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("ID de message invalide"));
        } catch (Exception e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Donnees de message invalides"));
        }
    }

    private boolean isCollectionPath(String pathInfo) {
        return pathInfo == null || "/".equals(pathInfo);
    }

    private boolean canAccessMessage(int idUtilisateur, Message message) {
        if (message == null) {
            return false;
        }

        Canal canal = canalDAO.findById(message.getIdCanal());

        return isPublicCanal(canal)
                || membreCanalDAO.isMembre(idUtilisateur, message.getIdCanal());
    }

    private boolean isPublicCanal(Canal canal) {
        return canal != null && "public".equalsIgnoreCase(canal.getTypeCanal());
    }
}
