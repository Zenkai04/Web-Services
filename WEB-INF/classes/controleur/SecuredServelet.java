package controleur;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.APIMessage;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import utils.JwtManager;

public abstract class SecuredServelet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected boolean checkAuthentication(
            HttpServletRequest req,
            HttpServletResponse res)
            throws IOException {

        String token = JwtManager.extractTokenFromRequest(req);

        if (token == null || !JwtManager.isValidToken(token)) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().print(objectMapper.writeValueAsString(
                    new APIMessage("Token manquant ou invalide")));
            return false;
        }

        return true;
    }

    protected int getAuthenticatedUserId(HttpServletRequest req) {
        String token = JwtManager.extractTokenFromRequest(req);
        return JwtManager.extractUserId(token);
    }

    protected void writeForbidden(HttpServletResponse res)
            throws IOException {
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().print(objectMapper.writeValueAsString(
                new APIMessage("Acces refuse")));
    }
}
