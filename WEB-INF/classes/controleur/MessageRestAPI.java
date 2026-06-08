package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CanalDAO;
import dao.DAOFactory;
import dao.MessageDAO;
import dto.Canal;
import dto.Message;
import dto.ApiMessage;
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
                            new ApiMessage("Message introuvable"));
                }
            } catch (NumberFormatException e) {
                writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                        new ApiMessage("ID de message invalide"));
            }
        } else {
            writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                    new ApiMessage("Chemin invalide"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                new ApiMessage("Méthode PUT non autorisée sur cette ressource"));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                new ApiMessage("Méthode DELETE non autorisée sur cette ressource"));    
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                new ApiMessage("Méthode PATCH non autorisée sur cette ressource"));
        }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
        throws IOException {
    writeJsonResponse(res, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
            new ApiMessage("Méthode POST non autorisée sur cette ressource"));
    }
}