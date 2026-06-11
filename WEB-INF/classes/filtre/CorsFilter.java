package filtre;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * FILTRE CORS - Autorisation des appels frontend vers l'API.
 *
 * Responsabilites :
 * - Ajouter les headers CORS sur toutes les reponses.
 * - Autoriser les methodes REST utilisees par le frontend.
 * - Autoriser le header Authorization necessaire aux tokens JWT.
 * - Repondre directement aux requetes OPTIONS de preflight.
 */
@WebFilter("/*")
public class CorsFilter implements Filter {

    /**
     * Intercepte toutes les requetes HTTP avant les servlets REST.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        // Headers requis pour permettre au frontend React d'appeler l'API Tomcat.
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Les requetes OPTIONS sont des preflight CORS : elles ne doivent pas atteindre les servlets.
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
