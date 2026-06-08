package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CanalDAO;
import dao.DAOFactory;
import dao.MessageDAO;
import dto.APIMessage;
import dto.Canal;
import dto.Message;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/canaux/*")
public class CanalRestAPI extends HttpServlet {

    private final CanalDAO canalDAO = DAOFactory.getInstance().getCanalDAO();
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
            List<Canal> canaux = canalDAO.findAll();
            writeJsonResponse(res, HttpServletResponse.SC_OK, canaux);
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length == 2) {
            handleGetCanal(parts[1], res);
            return;
        }

        if (parts.length == 3 && "messages".equals(parts[2])) {
            handleGetCanalMessages(parts[1], res);
            return;
        }

        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new APIMessage("URI invalide"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String pathInfo = req.getPathInfo();

        if (!isCollectionPath(pathInfo)) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("URI invalide pour la creation d'un canal"));
            return;
        }

        try {
            Canal canal = objectMapper.readValue(req.getReader(), Canal.class);
            boolean created = canalDAO.save(canal);

            if (!created) {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Creation du canal impossible"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_CREATED, canal);
        } catch (Exception e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("JSON invalide"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        String pathInfo = req.getPathInfo();

        if (isCollectionPath(pathInfo)) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("URI invalide pour la mise a jour d'un canal"));
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length == 2) {
            handleUpdateCanal(parts[1], req, res);
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
        String pathInfo = req.getPathInfo();

        if (isCollectionPath(pathInfo)) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("URI invalide pour la suppression d'un canal"));
            return;
        }

        String[] parts = pathInfo.split("/");

        if (parts.length != 2) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("URI invalide pour la suppression d'un canal"));
            return;
        }

        try {
            int idCanal = Integer.parseInt(parts[1]);
            Canal existingCanal = canalDAO.findById(idCanal);

            if (existingCanal == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Canal introuvable"));
                return;
            }

            boolean deleted = canalDAO.delete(idCanal);

            if (!deleted) {
                writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                        new APIMessage("Suppression du canal impossible"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK,
                    new APIMessage("Canal supprime"));
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de canal invalide"));
        }
    }

    private void handleGetCanal(String idCanalPart, HttpServletResponse res)
            throws IOException {
        try {
            int idCanal = Integer.parseInt(idCanalPart);
            Canal canal = canalDAO.findById(idCanal);

            if (canal == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Canal introuvable"));
                return;
            }

            writeJsonResponse(res, HttpServletResponse.SC_OK, canal);
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de canal invalide"));
        }
    }

    private void handleGetCanalMessages(String idCanalPart, HttpServletResponse res)
            throws IOException {
        try {
            int idCanal = Integer.parseInt(idCanalPart);
            Canal canal = canalDAO.findById(idCanal);

            if (canal == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Canal introuvable"));
                return;
            }

            List<Message> messages = messageDAO.findByCanal(idCanal);
            writeJsonResponse(res, HttpServletResponse.SC_OK, messages);
        } catch (NumberFormatException e) {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new APIMessage("Identifiant de canal invalide"));
        }
    }

    private void handleUpdateCanal(String idCanalPart, HttpServletRequest req,
            HttpServletResponse res) throws IOException {
        try {
            int idCanal = Integer.parseInt(idCanalPart);
            Canal existingCanal = canalDAO.findById(idCanal);

            if (existingCanal == null) {
                writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                        new APIMessage("Canal introuvable"));
                return;
            }

            Canal updatedCanal = objectMapper.readValue(req.getReader(), Canal.class);
            updatedCanal.setIdCanal(idCanal);

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

    private boolean isCollectionPath(String pathInfo) {
        return pathInfo == null || "/".equals(pathInfo);
    }
}
