package co.edu.unicauca.saberpro.question.domain.ports.outbound;

import co.edu.unicauca.saberpro.question.domain.model.Question;

/**
 * PUERTO DE SALIDA (driven): avisar a otros contextos que una pregunta fue enviada a revisión.
 * El dominio no sabe si detrás hay RabbitMQ, Kafka o un simple log.
 */
public interface NotificacionPort {

    void publicarEnvioARevision(Question pregunta, String operationId);
}
