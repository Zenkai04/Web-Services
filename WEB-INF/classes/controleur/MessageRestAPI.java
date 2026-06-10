package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DAOFactory;
import dao.MessageDAO;
import dto.Message;
import dto.APIMessage;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/messages/*")
public class MessageRestAPI extends HttpServlet {

    private final MessageDAO messageDAO = DAOFactory.getInstance().getMessageDAO();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private void writeJsonResponse(HttpServletResponse res, int status, Object data)
    throws IOException {

        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");

        String json = objectMapper.writeValueAsString(data);
        res.getWriter().print(json);    
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Message> messages = messageDAO.findAll();
            writeJsonResponse(res, HttpServletResponse.SC_OK, messages);
            return;
        }

        // Cas : GET /messages
        if (pathInfo.equals("/")) {
            List<Message> messages = messageDAO.findAll();
            writeJsonResponse(res, HttpServletResponse.SC_OK, messages);
            return;
        }

        // Cas : GET /messages/{id}
        String[] parts = pathInfo.split("/");

        if (parts.length == 2) {
            try {
                int idMessage = Integer.parseInt(parts[1]);
                Message message = messageDAO.findById(idMessage);
                if (message != null) {
                    writeJsonResponse(res, HttpServletResponse.SC_OK, message);
                } else {
                    writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                            new APIMessage("Message introuvable"));
                }
            } catch (NumberFormatException e) {
                writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                        new APIMessage("ID de message invalide"));
            }
        } else {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Chemin invalide"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de message manquant"));
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length != 2) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("URI invalide pour la mise à jour de message"));
            return;
        }

        try {
            int idMessage = Integer.parseInt(parts[1]);

            Message existingMessage = messageDAO.findById(idMessage);

            if (existingMessage == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Message introuvable"));
                return;
            }

            Message updatedMessage = objectMapper.readValue(req.getReader(), Message.class);
            updatedMessage.setIdMessage(idMessage);

            boolean updated = messageDAO.update(updatedMessage);

            if (!updated) {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Mise à jour du message impossible"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK, updatedMessage);

        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("ID de message invalide"));
        } catch (Exception e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Données de message invalides"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de message manquant"));
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length != 2) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("URI invalide pour la suppression de message"));
            return;
        }

        try {
            int idMessage = Integer.parseInt(parts[1]);

            boolean deleted = messageDAO.delete(idMessage);

            if (!deleted) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Message introuvable"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK,
                    new APIMessage("Message supprimé avec succès"));

        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("ID de message invalide"));
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
        throws IOException {

        String pathInfo = req.getPathInfo();

        if (pathInfo != null && !pathInfo.equals("/")) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("URI invalide pour la création de message"));
            return;
        }

        try {
            Message newMessage = objectMapper.readValue(req.getReader(), Message.class);

            boolean created = messageDAO.save(newMessage);

            if (created) {
                writeJsonResponse(res, HttpServletResponse.SC_CREATED, newMessage);
            } else {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Erreur lors de la création du message"));
            }
        } catch (IOException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Données de message invalides"));
        }
    }
}