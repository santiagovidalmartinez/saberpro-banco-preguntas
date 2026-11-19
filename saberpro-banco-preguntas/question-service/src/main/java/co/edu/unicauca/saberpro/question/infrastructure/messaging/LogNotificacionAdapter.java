package co.edu.unicauca.saberpro.question.infrastructure.messaging;

import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.ports.outbound.NotificacionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Adaptador alterno de NotificacionPort: solo escribe en el log (pruebas / sin RabbitMQ). */
public class LogNotificacionAdapter implements NotificacionPort {

    private static final Logger log = LoggerFactory.getLogger(LogNotificacionAdapter.class);

    @Override
    public void publicarEnvioARevision(Question pregunta, String operationId) {
        log.info("[sin RabbitMQ] revisión solicitada: operationId={} preguntaId={}",
                operationId, pregunta.getId().valor());
    }
}
