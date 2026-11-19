package co.edu.unicauca.saberpro.question.domain.ports.outbound;


/**
 * PUERTO DE SALIDA (driven): consulta síncrona al Validation Service.
 * Lanza ReglaNegocioException si la pregunta no es válida y ServicioNoDisponibleException
 * si el servicio no responde.
 */
public interface ValidacionPort {

    void validar(String enunciado, String justificacion, java.util.List<String> textosOpciones);
}
