package co.edu.unicauca.saberpro.question.domain.exception;

/** Se lanza cuando se intenta violar un invariante o regla de negocio. */
public class ReglaNegocioException extends DomainException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
