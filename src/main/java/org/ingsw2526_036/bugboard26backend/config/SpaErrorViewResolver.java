package org.ingsw2526_036.bugboard26backend.config;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

/**
 * Resolver per il fallback del routing SPA (Single Page Application - React Router pushState).
 * Quando una richiesta non trova un file statico fisico o un endpoint API (HTTP 404),
 * se si tratta di una richiesta GET e non è una chiamata API REST o una risorsa con estensione,
 * inoltra internamente a /index.html con status 200 OK.
 */
@Component
public class SpaErrorViewResolver implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        if (status == HttpStatus.NOT_FOUND && "GET".equalsIgnoreCase(request.getMethod())) {
            String path = request.getRequestURI();
            boolean hasFileExtension = path.substring(path.lastIndexOf('/') + 1).contains(".");
            if (!path.startsWith("/api")
                    && !path.startsWith("/v3/api-docs")
                    && !path.startsWith("/swagger-ui")
                    && !hasFileExtension) {
                return new ModelAndView("forward:/index.html", HttpStatus.OK);
            }
        }
        return null;
    }
}
