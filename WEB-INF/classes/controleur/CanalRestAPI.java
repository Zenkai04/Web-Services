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

@WebServlet("/canaux/*")
public class CanalRestAPI extends HttpServlet {
        
        private final CanalDAO canalDAO = DAOFactory.getInstance().getCanalDAO();
        private final MessageDAO messageDAO = DAOFactory.getInstance().getMessageDAO();

        private final ObjectMapper objectMapper = new ObjectMapper();

        private void writeJsonResponse(HttpServletResponse res, int status, Object data)
        throws IOException {

                res.setStatus(status);
                res.setContentType("application/json;charset=UTF-8");

                String json =
                        objectMapper.writeValueAsString(data);

                res.getWriter().print(json);
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse res)
                throws IOException {

        String pathInfo = req.getPathInfo();

        // Cas 1 : GET /canaux
        if (pathInfo == null || pathInfo.equals("/")) {
                List<Canal> canaux = canalDAO.findAll();
                writeJsonResponse(res, HttpServletResponse.SC_OK, canaux);
                return;
        }

        String[] parts = pathInfo.split("/");

        // Cas 2 : GET /canaux/{id}/messages
        if (parts.length == 3 && parts[2].equals("messages")) {
                try {
                int idCanal = Integer.parseInt(parts[1]);

                Canal canal = canalDAO.findById(idCanal);

                if (canal == null) {
                        writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                                new ApiMessage("Canal introuvable"));
                        return;
                }

                List<Message> messages = messageDAO.findByCanal(idCanal);

                writeJsonResponse(res, HttpServletResponse.SC_OK, messages);
                return;

                } catch (NumberFormatException e) {
                writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                        new ApiMessage("Identifiant de canal invalide"));
                return;
                }
        }

        // Cas 3 : GET /canaux/{id}
        if (parts.length == 2) {
                try {
                int idCanal = Integer.parseInt(parts[1]);

                Canal canal = canalDAO.findById(idCanal);

                if (canal == null) {
                        writeJsonResponse(res, HttpServletResponse.SC_NOT_FOUND,
                                new ApiMessage("Canal introuvable"));
                        return;
                }

                writeJsonResponse(res, HttpServletResponse.SC_OK, canal);
                return;

                } catch (NumberFormatException e) {
                writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                        new ApiMessage("Identifiant de canal invalide"));
                return;
                }
        }

        // Cas final : URI non reconnue
        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                new ApiMessage("URI invalide"));
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse res)
                throws IOException {

                String pathInfo = req.getPathInfo();

                // POST doit viser la collection : /canaux
                if (pathInfo != null && !pathInfo.equals("/")) {
                        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                                new ApiMessage("URI invalide pour la création d'un canal"));
                        return;
                }

                try {
                        Canal canal = objectMapper.readValue(req.getReader(), Canal.class);

                        boolean created = canalDAO.save(canal);

                        if (!created) {
                        writeJsonResponse(res, HttpServletResponse.SC_CONFLICT,
                                new ApiMessage("Création du canal impossible"));
                        return;
                        }

                        writeJsonResponse(res, HttpServletResponse.SC_CREATED, canal);

                } catch (Exception e) {
                        writeJsonResponse(res, HttpServletResponse.SC_BAD_REQUEST,
                                new ApiMessage("JSON invalide"));
                }
        }
}