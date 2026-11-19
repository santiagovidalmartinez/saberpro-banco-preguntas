package co.edu.unicauca.saberpro.lifecycle;

import co.edu.unicauca.saberpro.lifecycle.messaging.EventosRecibidos;
import co.edu.unicauca.saberpro.lifecycle.messaging.PreguntaRevisionEvent;
import co.edu.unicauca.saberpro.lifecycle.messaging.QuestionReviewListener;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class QuestionReviewListenerTest {

    @Test
    void guardaElEventoRecibido() {
        EventosRecibidos recibidos = new EventosRecibidos();
        QuestionReviewListener listener = new QuestionReviewListener(recibidos);
        PreguntaRevisionEvent e = new PreguntaRevisionEvent("op-1", "id-1", "¿Enunciado?", "PENDIENTE_REVISION", Instant.now());
        listener.alRecibirRevision(e);
        assertEquals(1, recibidos.todos().size());
        assertEquals("op-1", recibidos.todos().get(0).operationId());
    }

    @Test
    void tieneDockerfileConImagenOpenJdk17YPuerto8084() throws Exception {
        String c = Files.readString(Path.of("Dockerfile"));
        assertTrue(c.contains("FROM eclipse-temurin:17-jdk-jammy"));
        assertTrue(c.contains("EXPOSE 8084"));
    }
}
