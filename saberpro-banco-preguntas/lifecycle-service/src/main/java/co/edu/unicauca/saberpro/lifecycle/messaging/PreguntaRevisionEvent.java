package co.edu.unicauca.saberpro.lifecycle.messaging;

import java.time.Instant;

/** Contrato del mensaje JSON (mismos campos que publica el Question Service). */
public record PreguntaRevisionEvent(String operationId, String preguntaId, String enunciado,
                                    String estado, Instant ocurridoEn) {
}
