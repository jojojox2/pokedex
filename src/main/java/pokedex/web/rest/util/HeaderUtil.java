package pokedex.web.rest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    private static final Logger log = LoggerFactory.getLogger(HeaderUtil.class);

    private HeaderUtil() {
    }

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-pokedexApp-alert", message);
        headers.add("X-pokedexApp-params", param);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("Se ha creado un nuevo " + entityName + ": " + param, param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("Se ha actualizado el " + entityName + " " + param, param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("Se ha borrado el " + entityName + " " + param, param);
    }

    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        log.error("Error al insertar datos, {}", defaultMessage);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-pokedexApp-error", defaultMessage);
        headers.add("X-pokedexApp-params", entityName);
        return headers;
    }
}
