package co.edu.unicauca.saberpro.lifecycle.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consumidor AMQP: recibe la solicitud de revisión de una pregunta. */
@Component
public class QuestionReviewListener {

    private static final Logger log = LoggerFactory.getLogger(QuestionReviewListener.class);

    private final EventosRecibidos recibidos;

    public QuestionReviewListener(EventosRecibidos recibidos) {
        this.recibidos = recibidos;
    }

    @RabbitListener(queues = MessagingConstants.QUEUE)
    public void alRecibirRevision(PreguntaRevisionEvent evento) {
        log.info("[LIFECYCLE] Revisión recibida -> operationId={} preguntaId={} estado={} enunciado=\"{}\"",
                evento.operationId(), evento.preguntaId(), evento.estado(), evento.enunciado());
        recibidos.agregar(evento);
    }
}
