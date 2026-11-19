package co.edu.unicauca.saberpro.question.domain.ports.inbound;

import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionId;

import java.util.List;

/** PUERTO DE ENTRADA (driving): casos de uso de creación y envío a revisión. */
public interface CrearPreguntaUseCase {

    /** Valida con el Validation Service y crea una pregunta en estado BORRADOR. */
    Question crear(CrearPreguntaCommand comando);

    /**
     * Pasa la pregunta a PENDIENTE_REVISION si cumple todos los invariantes y publica el evento
     * para el Lifecycle Service. Devuelve el identificador de la operación asíncrona.
     */
    EnvioRevision enviarARevision(QuestionId id);

    /** Datos de una opción de respuesta. */
    record OpcionCommand(String texto, boolean correcta) {
    }

    /** Datos necesarios para crear una pregunta. */
    record CrearPreguntaCommand(String enunciado, String justificacion, List<OpcionCommand> opciones) {
    }

    /** Resultado del envío: pregunta ya en PENDIENTE_REVISION y la operación despachada. */
    record EnvioRevision(String operationId, Question pregunta) {
    }
}
