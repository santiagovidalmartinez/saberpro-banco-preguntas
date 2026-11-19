package co.edu.unicauca.saberpro.question.domain.exception;

/** Se lanza cuando se busca una pregunta que no existe. */
public class PreguntaNoEncontradaException extends DomainException {
    public PreguntaNoEncontradaException(String id) {
        super("No existe una pregunta con id " + id + ".");
    }
}
