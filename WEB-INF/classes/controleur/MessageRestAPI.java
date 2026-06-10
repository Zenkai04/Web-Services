package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DAOFactory;
import dao.MessageDAO;
import dto.APIMessage;
import dto.Message;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
        res.getWriter().print(objectMapper.writeValueAsString(data));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String pathInfo = req.getPathInfo();

        if (isCollectionPath(pathInfo)) {
            handleGetMessages(res);
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length == 2) {
            handleGetMessage(parts[1], res);
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("Chemin invalide"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                new APIMessage("La creation d'un message doit se faire via son canal"));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String pathInfo = req.getPathInfo();

        if (isCollectionPath(pathInfo)) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de message manquant"));
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length == 2) {
            handleUpdateMessage(parts[1], req, res);
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("URI invalide pour la mise a jour de message"));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                new APIMessage("La suppression d'un message doit se faire via son canal"));
    }

    private void handleGetMessages(HttpServletResponse res) throws IOException {
        List<Message> messages = messageDAO.findAll();
        writeJsonResponse(res, HttpServletResponse.SC_OK, messages);
    }

    private void handleGetMessage(String idMessagePart, HttpServletResponse res)
            throws IOException {
        try {
            int idMessage = Integer.parseInt(idMessagePart);
            Message message = messageDAO.findById(idMessage);

            if (message == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Message introuvable"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK, message);
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("ID de message invalide"));
        }
    }

    private void handleUpdateMessage(String idMessagePart, HttpServletRequest req,
            HttpServletResponse res) throws IOException {
        try {
            int idMessage = Integer.parseInt(idMessagePart);

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
}
