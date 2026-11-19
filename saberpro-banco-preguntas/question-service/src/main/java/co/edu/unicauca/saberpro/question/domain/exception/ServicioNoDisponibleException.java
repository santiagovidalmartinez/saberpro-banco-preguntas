package co.edu.unicauca.saberpro.question.domain.exception;

/** Un servicio externo (por ejemplo Validation Service o RabbitMQ) no respondió. */
public class ServicioNoDisponibleException extends DomainException {
    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
