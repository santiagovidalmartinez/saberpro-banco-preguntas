package co.edu.unicauca.saberpro.question.infrastructure.messaging;

import co.edu.unicauca.saberpro.question.domain.exception.ServicioNoDisponibleException;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.core.AmqpTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitMQPublisherTest {

    private static Question preguntaPendiente() {
        Question q = new Question();
        q.definirEnunciado("¿Cuál es la capital de Colombia?");
        q.definirJustificacion("Bogotá es la capital y sede del gobierno nacional.");
        q.agregarOpcion("Medellín", false);
        q.agregarOpcion("Bogotá", true);
        q.agregarOpcion("Cali", false);
        q.agregarOpcion("Popayán", false);
        q.enviarARevision();
        return q;
    }

    @Test
    void publicaElEventoEnElExchangeYLaRoutingKeyCorrectos() {
        AmqpTemplate plantilla = mock(AmqpTemplate.class);
        Question q = preguntaPendiente();
        new RabbitMQPublisher(plantilla).publicarEnvioARevision(q, "op-1");

        ArgumentCaptor<Object> mensaje = ArgumentCaptor.forClass(Object.class);
        verify(plantilla).convertAndSend(eq("pregunta.exchange"), eq("pregunta.revisar"), mensaje.capture());
        PreguntaRevisionEvent e = (PreguntaRevisionEvent) mensaje.getValue();
        assertEquals("op-1", e.operationId());
        assertEquals(q.getId().valor(), e.preguntaId());
        assertEquals("PENDIENTE_REVISION", e.estado());
    }

    @Test
    void siRabbitNoResponde_lanzaServicioNoDisponible() {
        AmqpTemplate plantilla = mock(AmqpTemplate.class);
        doThrow(new AmqpConnectException(new IOException("sin conexión")))
                .when(plantilla).convertAndSend(any(String.class), any(String.class), any(Object.class));
        assertThrows(ServicioNoDisponibleException.class,
                () -> new RabbitMQPublisher(plantilla).publicarEnvioARevision(preguntaPendiente(), "op-2"));
    }
}
