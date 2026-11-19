package co.edu.unicauca.saberpro.question.api.dto;

/** Respuesta 202 Accepted de POST /api/preguntas/{id}/enviar-revision. */
public record EnvioRevisionResponse(String operationId, String status, PreguntaResponse pregunta) {
}
