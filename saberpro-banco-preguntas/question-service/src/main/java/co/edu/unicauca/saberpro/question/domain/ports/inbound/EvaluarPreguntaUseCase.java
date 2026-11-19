package co.edu.unicauca.saberpro.question.domain.ports.inbound;

import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.model.QuestionId;

/** PUERTO DE ENTRADA (driving): resultado de la revisión de una pregunta. */
public interface EvaluarPreguntaUseCase {

    Question aprobar(QuestionId id);

    Question rechazar(QuestionId id);
}
