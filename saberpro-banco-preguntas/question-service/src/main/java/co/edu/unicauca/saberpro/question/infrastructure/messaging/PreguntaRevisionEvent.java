package co.edu.unicauca.saberpro.question.infrastructure.messaging;

import java.time.Instant;

/** Mensaje JSON que viaja por RabbitMQ hacia el Lifecycle Service. */
public record PreguntaRevisionEvent(String operationId, String preguntaId, String enunciado,
                                    String estado, Instant ocurridoEn) {
}
