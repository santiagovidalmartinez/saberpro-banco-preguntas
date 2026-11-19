package co.edu.unicauca.saberpro.question.infrastructure.rest;

import co.edu.unicauca.saberpro.question.domain.exception.ReglaNegocioException;
import co.edu.unicauca.saberpro.question.domain.exception.ServicioNoDisponibleException;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.ValidacionPort;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/** ADAPTADOR DE SALIDA (REST síncrono): consulta al Validation Service (:8082). */
public class ValidationRestClient implements ValidacionPort {

    private final RestTemplate http;
    private final String baseUrl;

    public ValidationRestClient(RestTemplate http, String baseUrl) {
        this.http = http;
        this.baseUrl = baseUrl;
    }

    public record RespuestaValidacion(boolean valido, List<String> errores) {
    }

    @Override
    public void validar(String enunciado, String justificacion, List<String> textosOpciones) {
        Map<String, Object> cuerpo = Map.of(
                "enunciado", enunciado == null ? "" : enunciado,
                "justificacion", justificacion == null ? "" : justificacion,
                "opciones", textosOpciones == null ? List.of() : textosOpciones);
        RespuestaValidacion r;
        try {
            r = http.postForObject(baseUrl + "/validate", cuerpo, RespuestaValidacion.class);
        } catch (RestClientException e) {
            throw new ServicioNoDisponibleException("Validation Service no disponible: " + e.getMessage());
        }
        if (r == null) {
            throw new ServicioNoDisponibleException("Validation Service respondió vacío.");
        }
        if (!r.valido()) {
            throw new ReglaNegocioException(String.join(" | ", r.errores()));
        }
    }
}
