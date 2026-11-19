package co.edu.unicauca.saberpro.question.infrastructure.messaging;

import co.edu.unicauca.saberpro.question.domain.exception.ServicioNoDisponibleException;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.NotificacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;

import java.time.Instant;

/** ADAPTADOR DE SALIDA (AMQP): publica el evento de revisión en RabbitMQ. */
public class RabbitMQPublisher implements NotificacionPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQPublisher.class);

    private final AmqpTemplate plantilla;

    public RabbitMQPublisher(AmqpTemplate plantilla) {
        this.plantilla = plantilla;
    }

    @Override
    public void publicarEnvioARevision(Question pregunta, String operationId) {
        PreguntaRevisionEvent evento = new PreguntaRevisionEvent(
                operationId,
                pregunta.getId().valor(),
                pregunta.getEnunciado().getTexto(),
                pregunta.getEstado().name(),
                Instant.now());
        try {
            plantilla.convertAndSend(MessagingConstants.EXCHANGE, MessagingConstants.ROUTING_KEY, evento);
            log.info("Evento publicado en RabbitMQ: operationId={} preguntaId={}", operationId, evento.preguntaId());
        } catch (AmqpException e) {
            throw new ServicioNoDisponibleException("No se pudo publicar en RabbitMQ: " + e.getMessage());
        }
    }
}
